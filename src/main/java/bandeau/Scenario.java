package bandeau;
import javax.xml.crypto.Data;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Classe utilitaire pour représenter la classe-association UML
 */
class ScenarioElement {

    Effect effect;
    int repeats;

    ScenarioElement(Effect e, int r) {
        effect = e;
        repeats = r;
    }

}
/**
 * Un scenario mémorise une liste d'effets, et le nombre de repetitions pour chaque effet
 * Un scenario sait se jouer sur un bandeau.
 */
public class Scenario {

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    private final List<ScenarioElement> myElements = new LinkedList<>();

    /**
     * Ajouter un effect au scenario.
     *
     * @param e l'effet à ajouter
     * @param repeats le nombre de répétitions pour cet effet
     */
    public void effect(Effect e, int repeats) {
        myElements.add(new ScenarioElement(e, repeats));
    }

    public void addEffect(Effect e, int repeats) { // Getter : read-only
        w.lock(); try { effect(e,repeats); } finally { w.unlock(); }
    }
    /**
     * Jouer ce scenario sur un bandeau
     *
     * @param b le bandeau ou s'afficher.
     */
    public void playOn(MonBandeau b) {
        Thread t = new Thread(){
            public void run(){
                play(b);
            }
        };
        t.start();
    }

    private void play(MonBandeau b) {
        try {
            r.lock();
            b.start();
            for (ScenarioElement element : myElements) {
                for (int repeats = 0; repeats < element.repeats; repeats++) {
                    element.effect.playOn(b);
                }
            }
        }catch (InterruptedException exception) {
        }finally {
            b.end();
            r.unlock();
        }

    }

}