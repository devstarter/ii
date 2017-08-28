import com.overzealous.remark.Remark
import org.junit.Assert
import org.junit.Test

class RemarkTest {
    @Test
    fun test() {
        Assert.assertEquals("[", Remark().convert("["))
    }
}