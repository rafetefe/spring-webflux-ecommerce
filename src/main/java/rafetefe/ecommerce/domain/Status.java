package rafetefe.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;


//@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Status {
//    @JsonProperty("COMPLETE")
    COMPLETE,
//    @JsonProperty("ONGOING")
    ONGOING,
//    @JsonProperty("CANCELLED")
    CANCELLED;

//    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
//    static Status find(String s) {
//        if(s.equals(405)){
//            return null;
//        }
//        return Status.valueOf(s);
//    }
}
