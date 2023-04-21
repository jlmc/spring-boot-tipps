delete
from todos
where true;

insert into todos (id,
                   title,
                   description,
                   version,
                   created,
                   updated)
values (99999,
        'Test container leaning',
        'Test container leaning in spring boot!!!',
        1,
        current_timestamp,
        current_timestamp);
