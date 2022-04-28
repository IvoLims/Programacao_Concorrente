-module(login_maneger).
-export([start/0, 
        create_account/2,
        close_account/2,
        login/2,
        logout/2,
        online/0]).

% 1. Escreva um módulo que implemente um gestor de logins que permita a criação de contas bem
% como controlar o login/logout de utilizadores: Deverão ser disponibilizadas nomeadamente as
% funções:

% create_account(Username, Passwd) -> ok | user_exists
% close_account(Username, Passwd) -> ok | invalid
% login(Username, Passwd) -> ok | invalid
% logout(Username) -> ok
% online() -> [Username]


%Consideremos User ->{Passwd, Online}

start() -> register(?MODULE,spawn(fun()-> loop(#{}) end)).
% ?MODULE equivalente a colocar o nome do módulo, faz isso por nós, o que permite a diminuição de erros e a versatabilidade do código
% Ou start() -> register(login_maneger,spawn(fun()-> loop(#{}) end)).
% Criamos um mapa e assumindo que se inicia a vazio, análogo a utilizar em java um HashMap.
% spawn devolve o pid e vamos guardar-lo no nome login_maneger
% Na prática, como todos estes dados são singleton, utiliza-se para o nome o nome do módulo.


create_account(User, Pass) -> ?MODULE ! {create_account, User, Pass, self()}, receive {Res,?MODULE} -> Res end.
% ! utilizado para mandar mensagem ao cliente
% Para o servidor poder responder é necessário enviar o nosso pid, utilizando self()
% receive Res -> Res end. (Recebiamos qualquer mensagem e aceitamos, o que pode provocar aceitar algo não vem do login_maneger)
% Ora, mas não sabemos o pid do outro processo do qual queremos receber a mensagem, ou seja, temos que utilizar o login_maneger


close_account(User, Pass) -> ?MODULE ! {close_account, User, Pass, self()}, receive{Res, ?MODULE} -> Res end. 

login(Username, Passwd) -> ?MODULE ! {login, User, Pass, self()}, receive{Res, ?MODULE} -> Res end. 

% Assumindo que o logout só é invocado se alguém já estiver feito login
logout(Username) -> ok

online() -> [Username]

% Fazer o mesmo para o outro lado do processo, que trata o receive
loop(Map) -> 
  receive 
        {create_account, User, Pass, From} ->
          %Vamos verificar se o User existe
          case maps:is_key(User,Pass) of
                true -> From ! {user_exists, ?MODULE}, 
                        loop(Map);
                false -> From ! {ok, ?MODULE},
                        loop(maps:put(User, {Pass, false}, Map))
                        % Ou: Map1 = maps:put(User, {Pass, false}, Map), loop(Map1)
          end;
        {close_account, User, Pass, From} ->
          case maps:find(User,Pass) of
                {ok,{Pass, _}} -> From ! {ok, ?MODULE}, 
                        loop(maps: remove(User,Map));
                _ -> From ! {invalid, ?MODULE},
                        loop(Map)
          end;
        {login, User, Pass, From} ->
          %Vamos verificar se o User existe
          case maps:find(User, Pass) of
                {ok,{Pass, false} -> From ! {ok, ?MODULE},
                        loop(maps: put(User,{Pass, true},Map);
                {ok,{Pass, true} -> From ! {ok, ?MODULE}, 
                        loop(Map);
                % Ou ???  {ok,{Pass, _} -> From ! {ok, ?MODULE}, 
                %        loop(maps: put(User,{Pass, true},Map);
                _ -> From ! {invalid, ?MODULE},
                        loop(Map)
          end;
        {{logout, User}, From} ->
          case maps:find(User, Pass) of
                {ok,{Pass, true} -> From ! {ok, ?MODULE},
                        loop(maps: put(User,{Pass, false},Map);
                _ -> loop(Map);
          end;
        {{online}, From} -> 
           F = fun(User, { _, true}, Acc) -> [User | Acc];
                  (_, _, Acc) -> Acc
               % Ou: 
               % fun(User, { _Pass, true}, Acc) -> [User | Acc];  O _ funciona como uma espécie de comentário, sendo que ignora
               %    (_User, {_Pass, _}, Acc) -> Acc;
               % end,
               Res = maps: fold(F,[],Map),
                        % Ou ainda, Res = [U || {U, {_P, true}} <-maps: to_list(Map)],
               From ! {Res, ?MODULO},
               loop(Map);
          end
  end.


% Outra versão do loop(Map), um servidor genérico
loop(Map) ->
        receive
                {Request, From} ->
                        {Res, NewMap} = handle(Request, Map),
                        From ! {Res, ?MODULE},
                        loop(NewMap)
        end.

% É necessário agora criar um handle para cada um dos casos

handle({create_account, User, Pass, From}, Map) ->
          case maps:is_key(User,Pass) of
                true -> 
                        {user, ?MODULE};
                false -> 
                        {ok, maps:put(User, {Pass, false}, Map))}
          end;

handle({close_account, User, Pass, From}, Map) ->
          case maps:find(User,Pass) of
                {ok,{Pass, _}} -> 
                        {ok, maps:remove(User,Map)}; 
                _ -> 
                        {invalid, Map}
          end;
handle(online,Map)->
        F = fun(User, { _, true}, Acc) -> [User | Acc];
                  (_, _, Acc) -> Acc
            Res = maps: fold(F,[],Map),
        
)


% Outra forma tornando genéricas as funções deste módulo:
%% invoke(Request) -> ?MODULE ! {Request, User, Pass, self()}, receive {Res,?MODULE} -> Res end.
%% create_account(User, Pass) -> invoke({create_account, User, Pass}).
%% close_account(User, Pass) -> invoke({close_account, User, Pass}).
%% login(User, Pass) -> invoke({login, User, Pass}).
%% logout(User) -> invoke({logout, User}).
%% online() -> invoke(online).


% loop(Map) -> 
%  receive 
%        {{create_account, User, Pass}, From} ->
          %Vamos verificar se o User existe
%          case maps:is_key(User,Pass) of
%                true -> From ! {user_exists, ?MODULE}, %Enviar mensagem a dizer que o utilizador existe, ora como já existe ficamos em loop(Map);
%                        loop(Map);
%                false -> From ! {ok, ?MODULE},
%                        loop(maps:put(User, {Pass, false}, Map))
%                        % Ou: Map1 = maps:put(User, {Pass, false}, Map), loop(Map1)
%          end;
%        {{close_account, User, Pass}, From} ->
%          case maps:find(User,Pass) of
%                {ok,{Pass, _}} -> From ! {ok, ?MODULE}, 
%                        loop(maps: remove(User,Map));
%                _ -> From ! {invalid, ?MODULE},
%                        loop(Map)
%          end;
%        {{login, User, Pass,} From} ->
          %Vamos verificar se o User existe
%          case maps:find(User, Pass) of
%                {ok,{Pass, false} -> From ! {ok, ?MODULE},
%                        loop(maps: put(User,{Pass, true},Map);
%                {ok,{Pass, true} -> From ! {ok, ?MODULE}, 
%                        loop(Map);
                % Ou ???  {ok,{Pass, _} -> From ! {ok, ?MODULE}, 
                %        loop(maps: put(User,{Pass, true},Map);
%                _ -> From ! {invalid, ?MODULE},
%                        loop(Map)
%          end;
%        {{logout, User}, From} ->
%          case maps:find(User, Pass) of
%                {ok,{Pass, true} -> From ! {ok, ?MODULE},
%                        loop(maps: put(User,{Pass, false},Map);
%                _ -> loop(Map);
%          end;
%        {{online}, From} -> F = fun(User, { _, true}, Acc) -> [User | Acc];
%                                   (_, _, Acc) -> Acc
%                            Res = maps: fold(F,[],Map),
%                            From ! {Res, ?MODULO},
%                            loop(Map);
%          end
%  end.
