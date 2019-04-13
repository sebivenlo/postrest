CREATE OR REPLACE FUNCTION  update_using_jsonb(tablename name, js jsonb, primcol name, cols name[])
RETURNS jsonb
LANGUAGE plpgsql as
$ update_using_jsonb$
  DECLARE
	pk integer;
	rec record;
  BEGIN
	SELECT CAST(js->>primcol as integer) INTO pk;
	select 
  end;
$update_using_jsonb$;
