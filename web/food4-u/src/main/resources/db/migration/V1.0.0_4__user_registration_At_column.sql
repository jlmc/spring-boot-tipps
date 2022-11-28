-- MySQL procedure
DELIMITER //
CREATE PROCEDURE AddColumn()
BEGIN
    # MySQL-style single line comment
    if not exists(
            select *
            from information_schema.columns
            where column_name = 'registration_at'
              and table_name = 'user'
              and table_schema = database()
        )
    then
        alter table user
            add column registration_at datetime(6);
    end if;

END //
DELIMITER;

CALL AddColumn();

DROP procedure AddColumn;
