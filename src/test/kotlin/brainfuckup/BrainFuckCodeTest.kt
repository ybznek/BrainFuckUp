package brainfuckup

import assertk.assertThat
import assertk.assertions.containsExactly
import brainfuckup.interpret.BrainFuckCode
import brainfuckup.interpret.Instruction
import brainfuckup.interpret.Instruction.*
import org.junit.Test

class BrainFuckCodeTest {

    @Test
    fun testAdd() {
        assertGenerated("+++", ChangeVal(3))
    }

    @Test
    fun testSub() {
        assertGenerated("---", ChangeVal(-3))
    }

    @Test
    fun testAddSubMerge() {
        assertGenerated("++-", ChangeVal(1))
    }

    @Test
    fun testSubAddMerge() {
        assertGenerated("--+", ChangeVal(-1))
    }

    @Test
    fun testPtrMove() {
        assertGenerated(">>>", MovePtr(3))
    }

    @Test
    fun testPtrMoveMerge() {
        assertGenerated(">>><", MovePtr(2))
    }

    @Test
    fun testPtrAndAdd() {
        assertGenerated(">>+", ChangeValPtr(2, 1))
    }

    @Test
    fun testPtrAndAddAdd() {
        assertGenerated(">>++", ChangeValPtr(2, 2))
    }

    @Test
    fun testPtrAndSub() {
        assertGenerated(">>-", ChangeValPtr(2, -1))
    }

    @Test
    fun testPtrAndAddSub() {
        assertGenerated(">>--", ChangeValPtr(2, -2))
    }

    @Test
    fun testPtrAndSetPositive() {
        assertGenerated(">>[-]+", SetValPtr(2, 1))
    }

    @Test
    fun testPtrAndSetPositive2() {
        assertGenerated(">>[-]++", SetValPtr(2, 2))
    }

    @Test
    fun testPtrAndSetNegative() {
        assertGenerated(">>[-]-", SetValPtr(2, -1))
    }

    @Test
    fun testPtrAndSetNegative2() {
        assertGenerated(">>[-]--", SetValPtr(2, -2))
    }

    @Test
    fun doubleSetValReset() {
        assertGenerated("[-]+++[-]++", SetVal(2))
    }

    @Test
    fun setValPtrSetValReset() {
        assertGenerated(">>>[-]+++[-]++", SetValPtr(3, 2))
    }

    @Test
    fun setValWrite() {
        assertGenerated("[-]+++.", SetValWrite(3))
    }

    @Test
    fun changeValWrite() {
        assertGenerated("+++.", ChangeValWrite(3))
    }

    @Test
    fun setValWriteString() {
        assertGenerated("[-].+.+.", SetValWriteString(arrayListOf(0,1,2)))
    }

    private fun assertGenerated(program: String, vararg elements: Instruction) {
        val instructions = getInstructions(program)
        assertThat(instructions).containsExactly(*elements)
    }

    private fun getInstructions(program: String): List<Instruction> {
        val brainFuckCode = BrainFuckCode()
        brainFuckCode.load(program)
        return brainFuckCode.instructions
    }
}