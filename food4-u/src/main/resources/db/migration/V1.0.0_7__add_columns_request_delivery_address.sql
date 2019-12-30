-- MySQL procedure
DELIMITER //
CREATE PROCEDURE AddColumn()
BEGIN
    if not exists(
            select *
            from information_schema.columns
            where column_name = 'delivery_address_street'
              and table_name = 'request'
              and table_schema = database()
        )
    then
            alter table request add column delivery_address_street varchar(1024) null;
    end if;

    if not exists(
            select *
            from information_schema.columns
            where column_name = 'delivery_address_city'
              and table_name = 'request'
              and table_schema = database()
        )
    then
        alter table request  add delivery_address_city varchar(100) null;
    end if;

    if not exists(
            select *
            from information_schema.columns
            where column_name = 'delivery_address_zip_code'
              and table_name = 'request'
              and table_schema = database()
        )
    then
        alter table request add delivery_address_zip_code varchar(100) null;

    end if;

    if not exists(
            select *
            from information_schema.columns
            where column_name = 'version'
              and table_name = 'request'
              and table_schema = database()
        )
    then
        alter table request add version tinyint(1) default 0 null;
    end if;

    if not exists(
            select *
            from information_schema.columns
            where column_name = 'delivery_at'
              and table_name = 'request'
              and table_schema = database()
        )
    then
        alter table request add delivery_at datetime(6) null;
    end if;

    if not exists(
            select *
            from information_schema.columns
            where column_name = 'canceled_at'
              and table_name = 'request'
              and table_schema = database()
        )
    then
        alter table request add canceled_at datetime(6) null;
    end if;

    if not exists(
            select *
            from information_schema.columns
            where column_name = 'confirmed_at'
              and table_name = 'request'
              and table_schema = database()
        )
    then
        alter table request add confirmed_at datetime(6) null;
    end if;

END //
DELIMITER;

CALL AddColumn();

DROP procedure AddColumn;
