import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.readText

typealias InputStrings = List<String>

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String): InputStrings =
    Path("src/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun InputStrings.filterNotEmpty(): InputStrings =
    filter(String::isNotEmpty)

fun BufferedImage.draw(doDraw: Graphics2D.() -> Any) = with(createGraphics()) {
    try {
        doDraw()
    } finally {
        dispose()
    }
}

fun saveVisualisation(image: BufferedImage) {
    ImageIO.write(image, "png", File("visualisation.png"))
}
