import java.util.concurrent.Semaphore

/* 1. Implemente um bounded buffer para uso concorrente, como uma classe que disponibilize os
métodos put e get, que bloqueiam quando o buffer está cheio/vazio respectivamente. O
buffer deverá usar um array de tamanho fixo N, passado no construtor.

class BoundedBuffer<T> {
BoundedBuffer(int N) { ... }
T get() throws InterruptedException { ... }
void put(T x) throws InterruptedException { ... }
}
*/

public class BoundedBuffer {
	private int[] buf;
	private int iget = 0;
	private int iput = 0;
	//private int len = 0; Não necessário pk o semáforo trata disto
	Semaphore items; //nº de items iniciais
	Semaphore slots; //slots livres
	Semaphore mutget = new Semaphore(1); // para exclusão mútua
	Semaphore mutset = new Semaphore(1);
    public BoundedBuffer(int N) {
	    items = new Semaphore(0); //Existem 0 items iniciais
    	slots = new Semaphore(N); //Existem N slots
    	buf = new int[N]; 
    }
    public int get() throws InterruptedException {
    	//if(len == 0) throw new InterruptedException();
    	int res;
    	mutget.acquire();
    	res = buf[iget];
    	iget = (iget +1) % buf.length;
    	mutget.release();
    	slots.release();
    	//len -= 1;
    	return res;
    }
    public void put(int v) throws InterruptedException {
    	slots.acquire();
    	mutput.acquire();
    	buf[iput] = v;
    	iput = (iput + 1) % buf.length;
    	//len += 1;
    	mutput.release();
    	items.release();
    }
}

//Para um tipo genérico
public class BoundedBuffer<T> {
	private T[] buf;
	private int iget = 0;
	private int iput = 0;
	//private int len = 0; Não necessário pk o semáforo trata disto
	Semaphore items; //nº de items iniciais
	Semaphore slots; //slots livres
	Semaphore mutget = new Semaphore(1); // para exclusão mútua
	Semaphore mutset = new Semaphore(1);
    public BoundedBuffer(int N) {
	    items = new Semaphore(0); //Existem 0 items iniciais
    	slots = new Semaphore(N); //Existem N slots
    	buf = new T[N]; 
    }
    public T get() throws InterruptedException {
    	//if(len == 0) throw new InterruptedException();
    	T res;
    	mutget.acquire();
    	res = buf[iget];
    	iget = (iget +1) % buf.length;
    	mutget.release();
    	slots.release();
    	//len -= 1;
    	return res;
    }
    public void put(T v) throws InterruptedException {
    	slots.acquire();
    	mutput.acquire();
    	buf[iput] = v;
    	iput = (iput + 1) % buf.length;
    	//len += 1;
    	mutput.release();
    	items.release();
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
	private int c = 0;
	private Semaphore mut = new Semaphore(1);
	private Semaphore sem = new Semaphore(0); // para o wait
	public Barreira (int N) { this.N = N; }
	public void await() throws InterruptedException{
		mut.acquire();
		c+=1;
		int v = c;
		mut.release();
		if(v < N){
			sem.acquire();
		}else{
			for(int i = 0; i<N; ++i){
				sem.release();
			}			
		}
		/* Outra forma:
		 
		if(v == N){
			for(int i = 0; i<N; ++i){
				sem.release();
			}
		sem.acquire(); 
	    }*/ 
	}
}

/* b) Modifique a implementação para permitir que a operação possa
 ser usada várias vezes por cada thread (barreira reutilizável), de modo a suportar
a sincronização no fim de cada uma de várias fases de computação. */
