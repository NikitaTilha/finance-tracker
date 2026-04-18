create table users (
    id bigserial primary key,
    username varchar(100) not null unique,
    password_hash varchar(255) not null,
    created_at timestamptz not null default now()
);

create table categories (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    name varchar(255) not null,
    type varchar(20) not null,
    created_at timestamptz not null default now(),
    unique (user_id, name, type)
);

create table transactions (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    category_id bigint references categories(id) on delete set null,
    amount_cents bigint not null,
    currency char(3) not null default 'RUB',
    occurred_at timestamptz not null,
    note varchar(500),
    created_at timestamptz not null default now()
);

create index idx_categories_user on categories(user_id);
create index idx_transactions_user_time on transactions(user_id, occurred_at);
create index idx_transactions_category on transactions(category_id);