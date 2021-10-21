import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.attrs
import react.dom.button
import react.dom.div
import react.dom.input

external interface WelcomeProps : RProps {
    var name: String
}

val Welcome = functionalComponent<WelcomeProps> { props ->
    val (name, setName) = useState(props.name)
    val (loading, setLoading) = useState(true)

    useEffect(listOf(name)) { //change from listOf to arrayOf
        console.log("I'm effect!")
    }

    console.log("I'm render!")
    if (!loading) {
        div {
            +"Hello, $name"
        }
        input {
            attrs {
                type = InputType.text
                value = name
                onChangeFunction = { event ->
                    setName((event.target as HTMLInputElement).value)
                }
            }
        }
        button {
            attrs {
                onClickFunction = {
                    it.preventDefault()
                    setLoading(true)
                }
            }
            +"Start loading"
        }

    } else {
        div {
            +"Loading..."
        }
        button {
            attrs {
                onClickFunction = {
                    it.preventDefault()
                    setLoading(false)
                }
            }
            +"Stop loading"
        }
    }
}
