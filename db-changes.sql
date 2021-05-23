alter table users
    add salt_edge_customer_id varchar;

create table connections
(
    id bigserial not null
        constraint connections_pk
            primary key,
    salt_edge_connection_id varchar not null,
    country_code varchar not null,
    customer_id varchar not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    deleted_at timestamp,
    secret varchar not null,
    provider_code varchar not null,
    provider_name varchar not null,
    status varchar,
    user_id bigint not null
        constraint users_id_fk
            references users,
    provider_connection_id bigint
        constraint provider_connections_id_fk
            references provider_connections
);

create unique index connections_salt_edge_connection_id_uindex
    on connections (salt_edge_connection_id);

create index connections_user_id_index
    on connections (user_id);

create index connections_provider_connection_id_index
    on connections (provider_connection_id);

create table accounts
(
    id bigint
        constraint accounts_pk
            primary key,
    account_id varchar,
    account_name varchar,
    nature int,
    currency_code varchar,
    balance numeric(12,2),
    created_at timestamp not null,
    card_type varchar,
    cards varchar,
    credit_limit numeric(12,2),
    status varchar,
    interest_income bigint,
    interest_amount bigint,
    profit_amount bigint,
    profit_rate bigint,
    asset_class bigint,
    product_type bigint,
    account_full_name varchar,
    fund_holding_investment_percentage varchar,
    fund_holding_balance numeric(12,2),
    fund_holding_bid_price numeric(12,2),
    fund_holding_value varchar,
    fund_holding_value_date timestamp,
    fund_holding_total_quality varchar,
    fund_holding_available_quality varchar,
    fund_holding_actual_price numeric(12,2),
    fund_holding_actual_value varchar,
    updated_at timestamp not null,
    connection_id bigint
        constraint accounts_connections_id_fk
            references connections
);

create index accounts_connection_id_index
    on accounts (connection_id);

create sequence accounts_id_seq;

alter table accounts alter column id set default nextval('public.accounts_id_seq');

alter sequence accounts_id_seq owned by accounts.id;

drop table account_connections;

create table account_connections
(
    id serial not null,
    account_id bigint
        constraint account_connections_accounts_id_fk
            references accounts,
    account_name varchar,
    nature int,
    currency varchar,
    balance numeric(12,2),
    source_id bigint,
    source_type varchar,
    provider_connection_id bigint
        constraint account_connections_provider_connections_id_fk
            references provider_connections,
    created_at timestamp not null,
    updated_at timestamp not null,
    deleted_at timestamp,
    saltedge_connection_id varchar
);

create index account_connections_account_id_index
    on account_connections (account_id);

create unique index account_connections_id_uindex
    on account_connections (id);

create index account_connections_provider_connection_id_index
    on account_connections (provider_connection_id);

create index account_connections_source_id_source_type_index
    on account_connections (source_id, source_type);

alter table account_connections
    add constraint account_connections_pk
        primary key (id);

alter table expense_sources
    add account_connection_id bigint;

create index expense_sources_account_connection_id_index
    on expense_sources (account_connection_id);

alter table expense_sources
    add constraint expense_sources_account_connections_id_fk
        foreign key (account_connection_id) references account_connections;

alter table expense_sources
    add card_type int;

alter table expense_categories
    add is_virtual boolean default false not null;

alter table income_sources
    add is_virtual boolean default false not null;

alter table transactions
    add salt_edge_transaction_id varchar;

alter table transactions
    add account_id bigint;

alter table transactions
    add constraint transactions_account_id_fk
        foreign key (account_id) references accounts;

create index transactions_account_id_index
    on transactions (account_id);

alter table provider_connections drop column connection_id;

drop index index_provider_connections_on_connection_secret;

alter table provider_connections drop column connection_secret;

alter table actives
    add account_connection_id bigint;

create index actives_account_connection_id_index
    on actives (account_connection_id);

alter table actives
    add constraint actives_account_connections_id_fk
        foreign key (account_connection_id) references account_connections;

drop index connections_provider_connection_id_index;

alter table connections drop constraint provider_connections_id_fk;

alter table connections drop column provider_connection_id;

alter table provider_connections
    add salt_edge_connection_id varchar;

create index provider_connections_salt_edge_connection_id_index
    on provider_connections (salt_edge_connection_id);

alter table provider_connections
    add constraint provider_connections_connections_salt_edge_connection_id_fk
        foreign key (salt_edge_connection_id) references connections (salt_edge_connection_id);

alter table transactions
    add active_id bigint;

create index transactions_active_id_index
    on transactions (active_id);

alter table transactions
    add constraint transactions_actives_id_fk
        foreign key (active_id) references actives;

alter table transactions
    add is_virtual_source boolean default false not null;

alter table transactions
    add is_virtual_destination boolean default false not null;

alter table transactions
    add is_borrow_or_return_source boolean default false not null;

alter table transactions
    add is_borrow_or_return_destination boolean default false not null;

alter table transactions
    add is_active_source boolean default false not null;

update transactionable_examples
set
    create_by_default = true
where transactionable_type = 'IncomeSource' or transactionable_type = 'ExpenseCategory';

alter table connections drop column deleted_at;

alter table transactions
    add profit bigint;

alter table users
	add planned_saving_percent bigint;

alter table exchange_rates rename column "from" to from_currency;

alter table exchange_rates rename column "to" to to_currency;

create index exchange_rates_from_currency_to_currency_index
	on exchange_rates (from_currency, to_currency);

alter table exchange_rates
	add is_updated boolean default false not null;

alter table connections
	add provider_id varchar;

alter table connections
	add provider_logo_url varchar;

drop index account_connections_provider_connection_id_index;

alter table account_connections drop constraint account_connections_provider_connections_id_fk;

alter table account_connections drop column provider_connection_id;

drop table provider_connections;

alter table account_connections
    drop column saltedge_connection_id;

alter table account_connections
    drop column deleted_at;

alter table account_connections drop column account_name;

alter table account_connections drop column nature;

alter table account_connections drop column currency;

alter table account_connections drop column balance;

alter table account_connections drop column updated_at;

alter table connections
	add next_refresh_possible_at timestamp;

alter table connections
	add interactive boolean;

-- ______________________________Prod DB on this level

alter table accounts alter column balance type bigint using balance::bigint;

alter table transactions
	add salt_edge_transaction_status int;

alter table connections
	add last_stage_status int;

alter table connections alter column salt_edge_connection_id drop not null;

create table hashes
(
	id bigserial not null,
	user_id bigint not null
		constraint hashes_users_id_fk
			references users,
	salt_edge_category varchar not null,
	category_id bigint
		constraint hashes_expense_categories_id_fk
			references expense_categories
);

create index hashes_category_id_index
	on hashes (category_id);

create unique index hashes_id_uindex
	on hashes (id);

create index hashes_user_id_salt_edge_category_index
	on hashes (user_id, salt_edge_category);

alter table hashes
	add constraint hashes_pk
		primary key (id);

alter table transactions
	add is_auto_categorized boolean default false not null;

alter table transactions
	add salt_edge_category varchar;

alter table transactions
	add is_duplicated boolean default false not null;

alter table transactions
	add is_duplication_actual boolean default false not null;

alter table connections alter column country_code drop not null;

create unique index transactions_salt_edge_transaction_id_user_id_uindex
    on transactions (salt_edge_transaction_id, user_id, deleted_at);

alter table transactions
    add is_changeable boolean default true not null;

create table metrics
(
    id bigserial not null
        constraint metric_pk
            primary key,
    name varchar,
    description varchar,
    navigation_key varchar,
    type int,
    parent_metric bigint,
    parent_order int
);

create table user_metrics
(
    id bigserial not null
        constraint user_metric_pk
            primary key,
    user_id bigint
        constraint user_metric_users_id_fk
            references users,
    metric_id bigint
        constraint user_metric_metric_id_fk
            references metrics,
    "order" int,
    is_hidden boolean,
    last_value int,
    last_display_value varchar
);

create table courses
(
    id bigserial not null,
    name varchar,
    description varchar,
    order_of_course int,
    country varchar,
    is_paid boolean,
    image_url varchar,
    icon_url varchar,
    days_time int,
    learned_times int,
    lessons_count int
);

create unique index course_id_uindex
    on courses (id);

alter table courses
    add constraint course_pk
        primary key (id);

create table lessons
(
    id bigserial not null,
    name varchar,
    description varchar,
    order_of_lesson int,
    is_paid boolean,
    image_url varchar,
    icon_url varchar,
    days_time int,
    learned_times int,
    parts_count int,
    course_id bigint
      constraint lessons_course_id_fk
         references courses
);

create unique index lessons_id_uindex
    on lessons (id);

alter table lessons
    add constraint lessons_pk
        primary key (id);

create table lesson_parts
(
    id bigserial not null,
    lesson_id int
        constraint lesson_parts_lessons_id_fk
            references lessons,
    text varchar,
    is_paid boolean,
    interaction_text varchar,
    content varchar,
    content_type varchar,
    order_of_lesson_part int
);

create unique index lesson_parts_id_uindex
    on lesson_parts (id);

alter table lesson_parts
    add constraint lesson_parts_pk
        primary key (id);

create table lesson_phrases
(
    id bigserial not null,
    lesson_id bigint
        constraint lesson_phrases_lessons_id_fk
            references lessons,
    text varchar
);

create unique index lesson_phrases_id_uindex
    on lesson_phrases (id);

alter table lesson_phrases
    add constraint lesson_phrases_pk
        primary key (id);

create table user_courses
(
    id bigserial not null,
    user_id bigint
        constraint user_courses_users_id_fk
            references users,
    course_id bigint
        constraint user_courses_course_id_fk
            references courses,
    learned_lessons_count int,
    is_learned boolean
);

create unique index user_courses_id_uindex
    on user_courses (id);

alter table user_courses
    add constraint user_courses_pk
        primary key (id);

create table user_lessons
(
    id bigserial not null,
    user_course_id bigint
        constraint user_lessons_user_courses_id_fk
            references user_courses,
    lesson_id bigint
        constraint user_lessons_lessons_id_fk
            references lessons,
    last_part_id bigint,
    learned_parts_count int,
    is_learned boolean
);

alter table user_lessons
    add constraint user_lessons_user_lesson_parts_id_fk
        foreign key (last_part_id) references user_lesson_parts;

create unique index user_lessons_id_uindex
    on user_lessons (id);

alter table user_lessons
    add constraint user_lessons_pk
        primary key (id);

create table user_lesson_parts
(
    id bigserial not null,
    user_lesson_id bigint
        constraint user_lesson_parts_user_lessons_id_fk
            references user_lessons,
    lesson_part_id bigint
        constraint user_lesson_parts_lesson_parts_id_fk
            references lesson_parts,
    is_learned boolean,
    is_interacted boolean
);

create unique index user_lesson_parts_id_uindex
    on user_lesson_parts (id);

alter table user_lesson_parts
    add constraint user_lesson_parts_pk
        primary key (id);





