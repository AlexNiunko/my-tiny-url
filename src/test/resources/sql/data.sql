TRUNCATE TABLE expire_url CASCADE;
TRUNCATE TABLE tiny_url CASCADE;


INSERT INTO tiny_url (id,url, tiny) VALUES
                                     (1,'https://news.google.com/topics/CAAqIggKIhxDQkFTRHdvSkwyMHZNREUxT1dabUVnSnlkU2dBUAE?hl=ru&gl=RU&ceid=RU:ru', 'grodnoNews'),
                                     (2,'https://www.gismeteo.by/weather-grodno-4243/month/', 'gismeteo'),
                                     (3,'https://www.google.com/', 'g7h8i9'),
                                     (4,'https://mvd.gov.by/ru/private/home/service/17','mvd');

SELECT SETVAL('tiny_url_id_seq',(SELECT MAX(id) from tiny_url));

INSERT INTO expire_url (id, expired_at) VALUES
                                            (1, NOW() - INTERVAL '1 day'),
                                            (2, NOW() + INTERVAL '1 day'),
                                            (3, NOW() + INTERVAL '5 minutes');

