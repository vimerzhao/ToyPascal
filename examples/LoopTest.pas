{测试3中循环结构}
PROGRAM LoopTest;

VAR
    root, number : real;
    i, sum : integer;
    n, pi : real;

BEGIN
    {牛顿法求平方根：测试while循环}
    number := 2;
    root := number;
    WHILE root*root - number > 0.00001 DO BEGIN
        root := (number/root + root)/2;
    END;
    writeln('root of 2: ', root);

    {求阶乘5!：测试FOR循环}
    sum := 1;
    FOR i := 1 TO 5 DO BEGIN
        sum := sum * i;
    END;
    writeln('5! = ', sum);{120}

    {利用公式求圆周率：测试REPEAT循环}
    n := 1;
    pi := 0;
    REPEAT
        pi := pi + (1/(4*n-3) - 1/(4*n-1));
        n := n+1;
    UNTIL n >= 1000;
    pi := 4*pi;
    writeln('PI = ', pi);

END.