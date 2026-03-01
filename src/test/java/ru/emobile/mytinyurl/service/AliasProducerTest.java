package ru.emobile.mytinyurl.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.emobile.mytinyurl.service.impl.AliasProducerImpl;

class AliasProducerTest {

    private final AliasProducer aliasProducer = new AliasProducerImpl();

    @Test
    void testGetAlias() {
        String url = "https://news.mail.ru/incident/69990471/?frommail=1&md=1";
        var alias = aliasProducer.getAlias(url);
        Assertions.assertTrue(alias.length() > 4);
    }

}
