import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Phones {
    private final List<Long> phones = Collections.synchronizedList(new ArrayList<>());

    public void add() {
        phones.add(randPhone());
    }

    public List<Long> getPhones() {
        return phones;
    }

    private long randPhone() {
        return 89000000000L + (long) (Math.random() * (999999999L - 100000000L)) + 100000000L;
    }
}