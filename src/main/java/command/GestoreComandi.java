package command;


import java.util.LinkedList;

//INVOKER DEI COMANDI
public class GestoreComandi
{
    private Command corrente;
    private final LinkedList<UndoableCommand> storiaComandi = new LinkedList<>();   //segue la logica LIFO

    public void setCommand(Command cmd)
    {   this.corrente = cmd;}


    public void esegui()
    {   corrente.execute();
        //se il comando è annullabile lo mette in cima dello stack della cronologia dei comandi eseguiti
        if (corrente instanceof UndoableCommand u) {
            storiaComandi.addFirst(u); // push
        }
    }

    public void annullaUltimoComando()
    {   if(!storiaComandi.isEmpty())
        {   UndoableCommand ultimo = storiaComandi.removeFirst();
            ultimo.undo();
        }
    }

    public boolean hasUndo()
    {   return !storiaComandi.isEmpty();
    }

    public void clearStory()
    {   storiaComandi.clear();
    }
}
