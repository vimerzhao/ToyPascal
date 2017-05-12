{ToyPascal支持的高级数据类型}
PROGRAM ConstructType;

VAR
    enum : (one, two, three, four);{枚举}
    subrange : 1..100;{子域}
    arr : ARRAY[1..10] OF integer;{数组}
    rec : RECORD{记录类型}
        name : ARRAY[1..10] OF char;
        age : integer;
        isStudent : boolean;
    END;

BEGIN
    enum := two;
    writeln(three < enum);{枚举不能直接输出}
    enum := four;
    writeln(three < enum);

    subrange := 10;{注意赋值不能越界}
    writeln(subrange);
    
    arr[1] := 11;{注意从1开始}
    writeln(arr[1]);{只能操作已经定义的值,输出arr[2]会导致错误}
    rec.name := 'Tony';
    rec.age := 10;
    writeln('My name is ', rec.name);
    writeln('I am ', rec.age, ' years old.');
END.