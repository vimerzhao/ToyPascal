PROGRAM hello(output);

{Write 'Hello, World.' ten times.}

VAR
    i : integer;

BEGIN {hello}
    FOR i := 1 TO 10 DO BEGIN
        writeln('Hello, World.');
    END
END {hello}
