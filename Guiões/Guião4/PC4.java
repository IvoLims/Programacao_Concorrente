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
	Semaphore slots //slots livres
	Semaphore mut = new Semaphore(1); // para exclusão mútua
    public BoundedBuffer(int N) {
	    items = new Semaphore(0); //Existem 0 items iniciais
    	slots = new Semaphore(N); //Existem N slots
    	buf = new int[N]; 
    }
    public int get() throws InterruptedException {
    	//if(len == 0) throw new InterruptedException();
    	int res;
    	mut.acquire();
    	res = buf[iget];
    	iget = (iget +1) % buf.length;
    	mut.release();
    	slots.release();
    	//len -= 1;
    	return res;
    }
    public void put(int v) throws InterruptedException {
    	slots.acquire();
    	mut.acquire();
    	buf[iput] = v;
    	iput = (iput + 1) % buf.length;
    	//len += 1;
    	mut.release();
    	items.release();
    }
}


/* 2. Considere um cenário produtor/consumidor sobre o BoundedBuffer do exercício anterior,
com P produtores e C consumidores, com um número total de threads C + P = N e
tempos de produção e consumo Tp e Tc. Obtenha experimentalmente o número óptimo de
threads de cada tipo a utilizar para maximizar o débito. */

/* 3. Escreva uma abstracção para permitir que N threads se sincronizem:
class Barreira {
Barreira (int N) { ... }
void await() throws InterruptedException { ... }
}
A operação await deverá bloquear até que as N threads o tenham feito; nesse momento
o método deverá retornar em cada thread. a) Suponha que cada thread apenas vai invocar
await uma vez sobre o objecto. b) Modifique a implementação para permitir que a operação possa ser usada várias vezes por cada thread (barreira reutilizável), de modo a suportar
a sincronização no fim de cada uma de várias fases de computação. */
