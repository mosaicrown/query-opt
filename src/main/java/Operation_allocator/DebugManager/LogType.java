package Operation_allocator.DebugManager;

public enum LogType {

    PLAN_ALTERNATIVE,       //registration of a final plan alternative

    ASSIGNMENT_CANDIDATES,  //number of assignment candidates found

    OPERATION_ASSIGNMENT,   //temporary assignment for an operation

    OPERATION_SIMULATION_FAILURE,   //operation didn't pass simulation check

    RELATION_PROFILE_FAILURE,       //relation profile found a failure

    ASSIGNMENT_CORRECTION,  //an operation fails cost correction

    ASSIGNMENT_VALIDATION,  //an operation passes correction

    STEP_COMPLETION,        //SQOD step completion

    GENERAL_INFO,           //general running info (usually configuration)

    /*
    INPUT_PARSER_ERROR,     //input parser exception

    OUTPUT_PARSER_ERROR,    //output parser exception
    */

    DATA_TRACE              //simple data trace report

}
