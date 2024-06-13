create or replace function update_project_expired() returns
    trigger as $$
begin
    update projects set expired = true where deadline <= now();
    return null;
end;
$$ language plpgsql;
create trigger update_project_expired_trigger
    before
        insert or delete
    on projects
execute function update_project_expired();

----------------------------------------------------------------

create or replace function update_task_expired() returns
    trigger as $$
begin
    update tasks set expired = true where deadline <= now();
    return null;
end;
$$ language plpgsql;
create trigger update_task_expired_trigger
    before
        insert or delete
    on tasks
execute function update_task_expired();