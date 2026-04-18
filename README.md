# finance-tracker

Backend REST API на Spring Boot для финансового трекера.

## Стек

- Java 17
- Maven
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Validation
- Actuator

## Что реализовано

- запуск Spring Boot приложения
- подключение PostgreSQL через Docker Compose
- миграции Flyway
- таблицы:
    - `users`
    - `categories`
    - `transactions`
- JPA entity и repository
- сервисный слой
- DTO и mapper
- REST API для:
    - категорий
    - транзакций
    - баланса
- валидация входных данных
- глобальная обработка ошибок

## Структура БД

### users
- `id`
- `email`
- `name`
- `created_at`

### categories
- `id`
- `user_id`
- `name`
- `type`
- `created_at`

### transactions
- `id`
- `user_id`
- `category_id`
- `amount_cents`
- `currency`
- `occurred_at`
- `note`
- `created_at`

