-module(myqueue).
-export([create/0, enqueue/2,dequeue/1]).

% 1. Escreva um módulo que implemente um tipo abstracto de dados queue, que permita inserir e
% remover itens com uma semântica FIFO. Deverão ser disponibilizadas as funções:

% create() -> Queue
% enqueue(Queue, Item) -> Queue
% dequeue(Queue) -> empty | {Queue, Item}

% Estas operações deverão permitir criar uma queue vazia, inserir um elemento e remover um elemento 
% (caso possível) respectivamente. Escreva um outro módulo que faça uso destas funções.

create()-> [].
enqueue(Queue, Item) -> Queue ++ [Item].
dequeue([]) -> empty;
dequeue([H | T]) -> {T, H}.

% Versão melhorada

create()-> {[][]}.
enqueue({In,Out}, Item) -> {[Item | In], Out}.
dequeue({[],[]}) -> empty;
dequeue({In, [H | T]}) -> {{In,T}, H}.
dequeue({In, []}) -> dequeue({[], lists:reverse(In)}).

reverse(L) -> reverse(L, []).
reverse([],A) -> A;
reverse([H|T],A) -> reverse(T,[H|A]).

% 2. Escreva um módulo que implemente um tipo abstracto de dados priorityqueue, que generalize uma queue
% introduzindo a noção de prioridade: um dequeue deve remover o item mais antigo da classe com maior prioridade.
% Deverão ser disponibilizadas as funções:

% create() -> PriQueue
% enqueue(PriQueue, Item, Priority) -> PriQueue
% equeue(PriQueue) -> empty | {PriQueue, Item}

