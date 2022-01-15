public class ToDo extends Task {
    public ToDo(String description) {
        super(description, TaskType.TODO);
    }

    @Override
    public String getSymbol() {
        return "T";
    }
}
