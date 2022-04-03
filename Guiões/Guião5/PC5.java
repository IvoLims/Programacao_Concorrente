/* 1. Implemente um bounded buffer para uso concorrente, como uma classe que disponibilize os
métodos put e get, que bloqueiam quando o buffer está cheio/vazio respectivamente. O
buffer deverá usar um array de tamanho fixo N, passado no construtor.

class BoundedBuffer<T> {
BoundedBuffer(int N) { ... }
T get() throws InterruptedException { ... }
void put(T x) throws InterruptedException { ... }
} */

public class BoundedBuffer {
	private int[] buf;
	private int iget = 0;
	private int iput = 0;
	private int nelems = 0;

    public BoundedBuffer(int N) {
    	buf = new int[N]; 
    }
    public synchronized int get() throws InterruptedException {
    	//while(!(nelems > 0)) equivalente
    	while(nelems == 0)
    		wait();
    	int res;
    	res = buf[iget];
    	iget = (iget +1) % buf.length;
    	nelems -= 1;
    	notifyAll(); /*acordar apenas uma não é correto pois não sei qual quer fazer get, tendo o risco 
    	de acordar uma que quer fazer put mas como o buffer está cheio não vou progredir, a melhor solução é acordar todos. */
    	return res;
    }
    public synchronized void put(int v) throws InterruptedException {
    	while(nelems == buf.length)
    		wait();
    	buf[iput] = v;
    	iput = (iput + 1) % buf.length;
    	nelems+=1;
    	notifyAll();
    }
}

//Para um tipo genérico
public class BoundedBuffer<T> {
	private T[] buf;
	private int iget = 0;
	private int iput = 0;
    public BoundedBuffer(int N) {
    	buf = new T[N]; 
    }
    public T get() throws InterruptedException {
    	T res;
    	res = buf[iget];
    	iget = (iget +1) % buf.length;
    	return res;
    }
    public void put(T v) throws InterruptedException {
    	buf[iput] = v;
    	iput = (iput + 1) % buf.length;
    }
}

/* 2. Considere um cenário produtor/consumidor sobre o BoundedBuffer do exercício anterior,
com P produtores e C consumidores, com um número total de threads C + P = N e
tempos de produção e consumo Tp e Tc. Obtenha experimentalmente o número óptimo de
threads de cada tipo a utilizar para maximizar o débito. */

//Para o tipo Int
public class Main{
	public static void main(String[] args){
		BoundedBuffer b = new  BoundedBuffer(20);
		
		new Thread(() -> {
			try{
				for(int i = 1;;++i){
					System.out.println("Vou fazer put\n");
					b.put(i);
					System.out.println("Fiz put de " + i);
					Thread.sleep(200);
					}
			}catch(InterruptedException e){}
		}).start();

		new Thread(() -> {
			try{
				for(int i = 1;;++i){
					System.out.println("Vou fazer get\n");
					int v = b.get();
					System.out.println("O get retornou "+v);
					Thread.sleep(200);
					}
			}catch(InterruptedException e){}
		}).start();
	}
}

/* 3. Escreva uma abstracção para permitir que N threads se sincronizem:

class Barreira {
Barreira (int N) { ... }
void await() throws InterruptedException { ... }
}

A operação await deverá bloquear até que as N threads o tenham feito; nesse momento
o método deverá retornar em cada thread. 

a) Suponha que cada thread apenas vai invocar await uma vez sobre o objecto. */

class Barreira {
	private final int N;
	private final int c = 0;   //contador de número de threads
	public Barreira (int N) { this.N = N; }
	public void await() throws InterruptedException{
		c+=1;
		if(c == N){
			notifyAll();
		}else while(c < N){
			wait();
		}
	}
}

/* b) Modifique a implementação para permitir que a operação 
possa ser usada várias vezes por cada thread (barreira reutilizável), de modo a suportar
a sincronização no fim de cada uma de várias fases de computação. */

class Barreira {
	private final int N;
	private int c = 0;   //contador de número de threads
	private boolean w = false; //wait
	public Barreira (int N) { this.N = N; }
	public synchronized void await() throws InterruptedException{
		c+=1;
		if(c == 1) w = true; // fechar a porta
		if(c == N){
			notifyAll();
			c = 0;
			w = false;
		}
		while(w){
			wait();
		}
	}
}

/* Falha quando a thread mais adiantada faz o await acorda todas consegue sair liberta a exclusao 
mutua se chamar o await outravez pode voltar a ganhar a exclusao mutua faz o c +1 e poe o w = true e 
as outras que estavam a acordar nao vao conseguir fazer nada . */

// Versão Correta

public class Barreira{
    public class Instance{
        int c = 0;
    }
    private final int N;
    private Instance e = new Instance(); //Representa uma etapa

    public Barreira(int N){ this.N = N;}

    public synchronized Instance await() throws InterruptedException{
        Instance e_snapshot = e; 
        e.c += 1;
    
        if(e.c==1){
            notifyAll();
            e.c = 0;
            e = new Instance();
        } else while(e == e_snapshot){
            wait(); 
        }
        return e_snapshot;
    }
}
