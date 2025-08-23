package command;


import java.util.LinkedList;

//INVOKER DEI COMANDI
public class GestoreComandi
{
    private final LinkedList<UndoableCommand> storiaComandi = new LinkedList<>();   //segue la logica LIFO

    public void esegui(Command cmd)
    {   cmd.execute();
        //se il comando è annullabile lo mette in cima dello stack della cronologia dei comandi eseguiti
        if (cmd instanceof UndoableCommand u) {
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
