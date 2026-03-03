<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>My Tiny url service – README</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        body { font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; line-height: 1.6; margin: 20px; max-width: 900px; }
        code, pre { font-family: "Fira Code", Menlo, Monaco, Consolas, monospace; background: #ddddd; }
        pre { padding: 12px; overflow-x: auto; border-radius: 4px; }
        h1, h2, h3 { margin-top: 1.4em; }
        a { color: #0366d6; }
        .block { margin-bottom: 1.2em; }
    </style>
</head>
<body>

<h1>My tiny url service</h1>

<p class="block">
    Небольшой сервис сокращения ссылок наподобие tinyurl, реализованный на
    <strong>Spring Boot</strong> с использованием <strong>PostgreSQL</strong>,
    <strong>Hibernate/JPA</strong>, миграций БД (Liquibase/Flyway), 
    <strong>Docker/Docker Compose</strong> и покрытый юнит‑ и интеграционными тестами.
</p>

<p class="block">
    Реализованы как основные требования, так и бонусные: alias для читаемых коротких ссылок и TTL
    для временных ссылок.
</p>

<hr>

<h2>Возможности</h2>

<ul>
    <li>Принимать длинные URL и возвращать короткие ссылки.</li>
    <li>Редиректить по короткой ссылке на исходный URL.</li>
    <li>Поддержка <strong>alias</strong> – читаемые короткие имена вместо случайного идентификатора.</li>
    <li>Поддержка <strong>TTL</strong> – время жизни ссылки (временные и «вечные» ссылки).</li>
    <li>Автоматическое удаление просроченных ссылок по расписанию.</li>
    <li>HTTP API (REST) + простой web‑интерфейс.</li>
    <li>Конфигурация через <code>application.yml</code>.</li>
    <li>Готовая dev‑среда через Docker/Docker Compose.</li>
</ul>

<hr>

<h2>Технологический стек</h2>

<ul>
    <li>Java 17+</li>
    <li>Spring Boot (Web, Validation, Scheduling)</li>
    <li>Spring Data JPA / Hibernate</li>
    <li>PostgreSQL</li>
    <li>Liquibase или Flyway (миграции БД)</li>
    <li>Docker, Docker Compose</li>
    <li>JUnit, Mockito, интеграционные тесты с Testcontainers</li>
    <li>Maven или Gradle</li>
</ul>

<hr>

<h2>Архитектура и компоненты</h2>

<p class="block">Приложение разделено на несколько основных слоев:</p>

<ul>
    <li><strong>REST‑контроллер</strong> – принимает и отдает JSON, реализует HTTP API.</li>
    <li><strong>Web‑контроллер</strong> – отдает HTML‑страницы/формы для работы через браузер.</li>
    <li><strong>Сервисный слой</strong> – бизнес‑логика создания, чтения и удаления ссылок, работа с TTL и alias.</li>
    <li><strong>Слой данных</strong> – JPA‑сущности и репозитории для работы с PostgreSQL.</li>
    <li><strong>Планировщик (Scheduler)</strong> – периодически удаляет просроченные временные ссылки.</li>
</ul>

<h3>DTO для создания ссылки</h3>

<pre><code>public record TinyUrlRequest(
        String url,
        Long ttl,
        String alias
) { }
</code></pre>

<ul>
    <li><code>url</code> – обязательное поле, исходная длинная ссылка (валидируется как URL).</li>
    <li><code>ttl</code> – необязательное время жизни ссылки (единицы времени можно описать в документации API); при <code>null</code> ссылка считается «вечной».</li>
    <li><code>alias</code> – необязательный человекочитаемый псевдоним; если не задан, сервис генерирует идентификатор сам.</li>
</ul>

<hr>

<h2>Генерация alias</h2>

<p class="block">
    Если пользователь не передал <code>alias</code>, сервис генерирует его автоматически на основе URL и текущего времени.
    Это делает alias компактным, URL‑дружественным и достаточно уникальным.
</p>

<p class="block"><strong>Алгоритм (класс <code>AliasProducerImpl</code>):</strong></p>

<ol>
    <li>Берется <code>hashCode()</code> исходного URL и переводится в беззнаковый <code>long</code>, чтобы избежать отрицательных значений.</li>
    <li>Полученное число конвертируется в base62‑строку (символы <code>0‑9</code>, <code>a‑z</code>, <code>A‑Z</code>).</li>
    <li>Из этой строки берется «ядро» длиной до 8 символов. Если длина меньше 4 символов – оно дополняется нулями до длины 4.</li>
    <li>Вычисляется дополнительный «временной» хеш: <code>System.currentTimeMillis() ^ positive</code>, который также переводится в base62.</li>
    <li>Из временной части берется небольшой суффикс (до 2 символов) и при возможности добавляется к ядру так, чтобы итоговая длина не превышала 8 символов.</li>
    <li>Результат обрезается до максимум 8 символов – это и есть короткий alias.</li>
</ol>

<p class="block">
    В итоге alias:
</p>
<ul>
    <li>зависит от исходного URL;</li>
    <li>дополняется текущим временем;</li>
    <li>состоит из безопасных для URL символов (base62);</li>
    <li>имеет ограниченную длину (до 8 символов).</li>
</ul>

<hr>

<h2>TTL и удаление ссылок</h2>

<ul>
    <li>Если при создании ссылки <code>ttl</code> не задан, запись хранится в базе без ограничения по времени.</li>
    <li>Если <code>ttl</code> задан, для ссылки вычисляется момент истечения (например, <code>createdAt + ttl</code>), и после этого она считается недействительной.</li>
    <li>При обращении к ссылке имеющей <code>ttl</code> - он проверяется, и если ссылка просрочена пользователю отправляется соответствующий ответ, затем ссылка удаляется.</li>
    <li>В приложении настроен планировщик, который периодически удаляет все просроченные записи из базы.</li>
</ul>

<h3>Настройки планировщика</h3>

<pre><code>scheduler:
  pool-size: 3
  interval-ms: 1000000
</code></pre>

<ul>
    <li><code>pool-size</code> – размер пула потоков для задач планировщика.</li>
    <li><code>interval-ms</code> – период запуска задачи очистки (в миллисекундах).</li>
</ul>

<hr>

<h2>REST API (пример)</h2>

<h3>Создание короткой ссылки</h3>

<p><strong>POST</strong> <code>/api/urls</code></p>

<p>Пример тела запроса:</p>

<pre><code>{
  "url": "https://example.com/some/very/long/link",
  "ttl": 3600000,
  "alias": "my-link"
}
</code></pre>

<p>Пример ответа:</p>

<pre><code>{
  "shortUrl": "https://your-host/my-link",
  "expiresAt": "2026-03-03T12:00:00Z"
}
</code></pre>

<ul>
    <li>Если <code>alias</code> не передан – будет сгенерирован автоматически.</li>
    <li>Если <code>ttl</code> не передан – ссылка считается «вечной», а поле <code>expiresAt</code> может отсутствовать или быть <code>null</code>.</li>
</ul>

<h3>Переход по короткой ссылке</h3>

<p><strong>GET</strong> <code>/{alias}</code></p>

<ul>
    <li>При обращении к <code>https://your-host/{alias}</code> сервис ищет соответствующую запись в БД.</li>
    <li>Если запись существует и срок действия не истек – возвращается HTTP‑редирект (302/301) на исходный URL.</li>
    <li>Если ссылка не найдена или просрочена – возвращается cоответствующий ответ в формате html.</li>
</ul>

<hr>

</body>
</html>
