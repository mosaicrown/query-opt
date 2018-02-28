package Trees.Semantics.SemanticExceptions;

public class RelationalOperationArityIncorrectException extends RuntimeException{

    public String getMessage(){
        return new String("Incorrect arity of relational operation");
    }

}
