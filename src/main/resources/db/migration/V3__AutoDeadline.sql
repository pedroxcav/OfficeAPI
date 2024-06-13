/*set global event_scheduler = on;

create event update_project_expired on schedule every 1 second
comment 'Event created to update the expiration state of project entities that had the deadline expired'
do update projects set expired = true where deadline <= now();

create event update_task_expired on schedule every 1 second
comment 'Event created to update the expiration state of task entities that had the deadline expired'
do update tasks set expired = true where deadline <= now();*/