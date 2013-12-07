package boundless;

import distorm.*;

import java.nio.ByteBuffer;

public class Loader {
    public static void main() {
        System.out.println("Starboundless: Reading 0x634450");
        ByteBuffer CoinItem_value = Native.getByteBuffer(0x634450, 100);

        CodeInfo info = new CodeInfo(0x634450, CoinItem_value, DecodeType.Decode32Bits, 0);
        DecomposedResult result = new DecomposedResult(100);

        System.out.println("Starboundless: Decomposing 0x634450");
        Distorm.decompose(info, result);

        System.out.println("Starboundless: Iterating instructions...");
        for(DecomposedInst inst : result.instructions) {
            if(inst == null) break;
            DecodedInst decodedInst = Distorm.format(info, inst);
            System.out.println("0x" + Long.toHexString(decodedInst.getOffset()) + ": " + decodedInst.getMnemonic() + " \t" + decodedInst.getOperands());
        }

        System.out.println("Starboundless: Resuming launch...");
        Native.resumeLaunch();
    }
}
