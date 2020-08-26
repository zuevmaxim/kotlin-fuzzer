package kotlinx.fuzzer.tests.java.imageio

import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO


class JavaImageTest {
    fun test(bytes: ByteArray) = try {
        val image = ImageIO.read(ByteArrayInputStream(bytes)) ?: throw IOException()
        val rotated = rotateImag(image, 90)
        check(rotated.type == image.type)
        1
    } catch (_: IOException) {
        0
    }


    private fun rotateImag(imag: BufferedImage, n: Int): BufferedImage {
        val rotationRequired = Math.toRadians(n.toDouble())
        val locationX = imag.width / 2.0
        val locationY = imag.height / 2.0
        val tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY)
        val op = AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR)
        val newImage = BufferedImage(imag.width, imag.height, imag.type)
        op.filter(imag, newImage)
        return newImage
    }
}
