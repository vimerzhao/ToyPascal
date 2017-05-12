{ToyPascal支持的基本数据类型}
PROGRAM BasicType;

{常量的类型由右值决定}
CONST
    WEEKDAY = 7;{整型常量}
    PI = 3.1415926;{浮点常量}
    X = 'x';{字符常量}
    ERROR = 'There is an error!';{字符串常量}
    YES = true;{布尔常量}
{TYPE可以命名新的常量,类似C语言的typedef}
TYPE
    string = char;
    bool = boolean;
{必须在变量定义后指定类型}
VAR
    message : char;
    grade : real;
    age : integer;
    result : boolean;

BEGIN
    writeln(WEEKDAY);
    writeln(PI);
    writeln(X);
    writeln(ERROR);
    writeln(YES);
    message := X;
    writeln(message);   
END.