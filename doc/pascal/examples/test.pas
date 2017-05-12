PROGRAM test;

VAR
    x:integer;

PROCEDURE func(VAR x: integer);
    BEGIN
        x := 2;
    END;

BEGIN
    x := 1;
    func(x);
    writeln(x);
END.
