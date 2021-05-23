# Skrudzh-API
## Для локального запуска нужно
1. Скачать [Java 13](https://www.oracle.com/java/technologies/javase-jdk13-downloads.html), [PostgreSQL](https://www.postgresql.org/download/)
2. [Скачать проект](https://bitbucket.org/stanum_llc/skrudzh-api/src)
3. Создать базу. Заполнить данными дампа
4. В файле [application.yml](/src/main/resources/application.yml), уточнить
коннект к БД в spring.datasource
5. Создать папку в /var/log/ с названием SkrudzhAPI
6. Запустить командой в терминале из корня проекта 
./gradlew bootRun