package wepayu;
import java.util.Stack;

public class Invoker
{
    private Stack <Command> historicoUndo;
    private Stack <Command> historicoRedo;

    public Invoker()
    {
        historicoUndo = new Stack<>();
        historicoRedo = new Stack<>();
    }

    public void executeCommand(Command command) throws Exception
    {
        command.execute();
        historicoUndo.push(command);
    }

    public Command getLastCommandUndo()
    {
        if(historicoUndo.size() > 0)
        {
            Command command = historicoUndo.pop();
            return command;
        }
        return null;
    }

    public Command getLastCommandRedo()
    {
        if(historicoRedo.size() > 0)
        {
            Command command = historicoRedo.pop();
            return command;
        }
        return null;
    }

    public void undoLastCommand() throws Exception
    {
        Command command = getLastCommandUndo();
        command.undo();
        historicoRedo.push(command);
    }

    public void redoLastCommand() throws Exception
    {
        Command command = getLastCommandRedo();
        command.execute();
        historicoUndo.push(command);
    }

    public Stack<Command> getHistoricoUndo() {
        return historicoUndo;
    }

    public Stack<Command> getHistoricoRedo()
    {
        return historicoRedo;
    }


}
