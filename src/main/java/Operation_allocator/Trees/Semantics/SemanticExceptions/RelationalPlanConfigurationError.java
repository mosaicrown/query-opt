package Operation_allocator.Trees.Semantics.SemanticExceptions;

public class RelationalPlanConfigurationError extends RuntimeException{

    public String getMessage(){
        return new String("Semantic error caused by: failure with relational algebra or grammar");
    }

}
