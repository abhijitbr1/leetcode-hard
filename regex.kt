class RegexSolution {

    var states: List<State> = ArrayList<State>(1)
    var s: String = ""

    fun isMatch(s: String, p: String): Boolean {
        states = createStates(p)
        this.s = s

        if (!quickCheck(s, states)) {
            return false
        }

        return isMatchInternal(0, 0)
    }

    fun quickCheck(s: String, states: List<State>): Boolean {
        for (state in states) {
            if (!state.canConsumeIndefinitely()) {
                if (!s.any { state.consume(it) }) {
                    return false
                }
            }
        }

        for (character in s) {
            if (!states.any { it.consume(character) }) {
                return false
            }
        }
        return true
    }
    fun isMatchInternal(strIndex: Int, stateIndex: Int): Boolean {
        //consumed entire string
        if (strIndex == s.length) {
            if (stateIndex == states.size) {
                return true
            }
            else if (states[stateIndex].canConsumeIndefinitely()) {
                return isMatchInternal(strIndex, stateIndex + 1)
            } else {
                return false
            }
        }
        
        //exhausted all states
        if (stateIndex == states.size && strIndex != s.length) {
            return false
        }
        
        val state = states[stateIndex]
        val character = s[strIndex]
        if (state.canConsumeIndefinitely()) {
            
            //try with consuming character
            var result = state.consume(character)
            
            if (result) {
                //consume character, stay in state
                result = isMatchInternal(strIndex + 1, stateIndex)
                //return successful match, or consume and next state, or empty transition
                return result || isMatchInternal(strIndex + 1, stateIndex + 1) || isMatchInternal(strIndex, stateIndex + 1)
            } else {
                //character didnt match state
                //try without consuming character (empty transition)
                return isMatchInternal(strIndex, stateIndex + 1)
            }        
        } else {
        //consume a character
        if (state.consume(character)) {
            //next character, next state
            return isMatchInternal(strIndex + 1, stateIndex + 1)
        } else {
            //character didnt match state
            return false
        }
        }     
        
        return false
    }

    fun createStates(p: String): List<State> {
        val list = ArrayList<State>(20)
        var listIndex = 0
        for (i in p.indices) {
            val canRepeatIndefinitely = (i < p.length - 1) && p[i+1] == '*'
            when (p[i]) {
                '*' -> {
                    // do nothing
                }
                '.' -> {
                    val state = State.AnyCharState(canRepeatIndefinitely)
                    list.add(state)
                }
                else -> {
                    val state = State.CharState(canRepeatIndefinitely, p[i])
                    list.add(state)
                }
            }
        }
        return list
    }

    sealed class State {
        abstract val flag: Boolean

        data class CharState(
            override val flag: Boolean,
            val character: Char,
        ) : State() {
            override fun consume(character: Char): Boolean {
                return this.character == character
            }
        }

        data class AnyCharState(
            override val flag: Boolean
        ) : State() {
            override fun consume(character: Char): Boolean {
                return true
            }        
        }
        
        fun canConsumeIndefinitely(): Boolean { return this.flag }
        abstract fun consume(character: Char): Boolean 
    }
}
// https://pl.kotl.in/IqE9IvOE2