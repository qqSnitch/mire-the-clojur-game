#!/usr/bin/env swipl

:- initialization(main, main).
:- use_module(library(socket)).
:- use_module(library(random)).
:- use_module(library(thread)).
:- dynamic server_messages/1.

main :-
    sleep(5),
    client(localhost, 3333).

client(Host, Port) :-
    setup_call_cleanup(
        tcp_connect(Host:Port, Stream, []),
        (   thread_create(reader_thread(Stream), _, [detached(true)]),
            bot(Stream)
        ),
        close(Stream)
    ).

% Инициализация хранилища сообщений
:- assertz(server_messages([])).

% Поток для чтения сообщений от сервера (без вывода в консоль)
reader_thread(Stream) :-
    repeat,
    (   read_line_to_string(Stream, Line),
        (   Line == end_of_file
        ->  true, !
        ;   retract(server_messages(Current)),
            append(Current, [Line], New),
            assertz(server_messages(New)),
            fail
        )
    ).

% Получить текущие сообщения от сервера
get_server_messages(Messages) :-
    server_messages(Messages).

% Получить последнее сообщение от сервера
get_last_message(Last) :-
    server_messages(Messages),
    (   Messages = [] -> Last = "";
        last(Messages, Last)
    ).

% Очистить хранилище сообщений
clear_server_messages :-
    retractall(server_messages(_)),
    assertz(server_messages([])).

% Получить последние n символов
get_last_chars(String,N,LastChars) :-
    string_length(String,Len),
    StartPos is max(0,Len-N),
    sub_string(String,StartPos,N,0,LastChars).
    

bot(Stream) :-
    % Регистрация бота
    format(Stream,'~s~n',["bot"]),
    flush_output(Stream),
    sleep(2),
    format(Stream,'~s~n',["say Hello everyone, bot is here!"]),
    flush_output(Stream),
    sleep(1),

    % Основной цикл отправки команд
    repeat,
    (   
        get_last_message(Last),
        get_last_chars(Last,10,Result),
        (
            Result = "Hello bot!"
            ->  format('Bot answer~n', []),
                sleep(1),
                string_length(Last,Len),
                NewLen is max(0, Len-14),
                sub_string(Last,2,NewLen,_,Name),
                atomic_list_concat(["say Hello there ", Name], Msg),
                format(Stream, '~s~n', [Msg]),
                flush_output(Stream),
                sleep(1)
            ;   true
        ),
        
        % Очищаем сообщения после обработки
        clear_server_messages,
        
        sleep(1),
        fail  % Продолжаем цикл
    ).
