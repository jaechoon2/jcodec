package org.jcodec.codecs.mpeg12.bitstream;

import java.io.IOException;

import org.jcodec.common.io.BitReader;
import org.jcodec.common.io.BitWriter;
import org.jcodec.common.model.Point;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 * 
 * @author The JCodec project
 * 
 */
public class PictureDisplayExtension {
    public Point[] frame_centre_offsets;

    public static PictureDisplayExtension read(BitReader bits, SequenceExtension se, PictureCodingExtension pce) {
        PictureDisplayExtension pde = new PictureDisplayExtension();
        pde.frame_centre_offsets = new Point[numberOfFrameCentreOffsets(se, pce)];
        for (int i = 0; i < pde.frame_centre_offsets.length; i++) {
            int frame_centre_horizontal_offset = bits.readNBit(16);
            bits.read1Bit();
            int frame_centre_vertical_offset = bits.readNBit(16);
            bits.read1Bit();
            pde.frame_centre_offsets[i] = new Point(frame_centre_horizontal_offset, frame_centre_vertical_offset);
        }
        return pde;
    }

    private static int numberOfFrameCentreOffsets(SequenceExtension se, PictureCodingExtension pce) {
        if (se == null || pce == null)
            throw new IllegalArgumentException("PictureDisplayExtension requires SequenceExtension"
                    + " and PictureCodingExtension to be present");
        if (se.progressive_sequence == 1) {
            if (pce.repeat_first_field == 1) {
                if (pce.top_field_first == 1)
                    return 3;
                else
                    return 2;
            } else {
                return 1;
            }
        } else {
            if (pce.picture_structure != PictureCodingExtension.Frame) {
                return 1;
            } else {
                if (pce.repeat_first_field == 1)
                    return 3;
                else
                    return 2;
            }
        }
    }

    public void write(BitWriter out) throws IOException {
        for (Point point : frame_centre_offsets) {
            out.writeNBit(point.getX(), 16);
            out.writeNBit(point.getY(), 16);
        }
    }
}