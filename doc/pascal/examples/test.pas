PROGRAM Test;
TYPE
    arrType = ARRAY[1..20] OF integer;
VAR
    arr : arrType;

PROCEDURE func(a:arrType);
    BEGIN
	arr[1] := 2;
    END;

BEGIN
    arr[1] := 1;
    writeln(arr[1]);
    func(arr);
    writeln(arr[1]);
END.
