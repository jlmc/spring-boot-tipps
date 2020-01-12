alter table payment_method add last_modification_at datetime(6) null;
update payment_method set last_modification_at = utc_timestamp where last_modification_at is null ;
alter table payment_method modify last_modification_at datetime(6) not null;