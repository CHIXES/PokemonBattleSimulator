package util;

import model.PokemonType;

public class TypeEffectiveness {

    public static double getMultiplier(PokemonType attacker, PokemonType defender) {
        if (attacker == null || defender == null) return 1.0;

        // ===== 攻击方：火 =====
        if (attacker == PokemonType.FIRE) {
            if (defender == PokemonType.GRASS) return 2.0;      // 火克草
            if (defender == PokemonType.FIRE) return 0.5;       // 火抵抗火
            if (defender == PokemonType.WATER) return 0.5;      // 火被水抵抗
            return 1.0;
        }

        // ===== 攻击方：水 =====
        if (attacker == PokemonType.WATER) {
            if (defender == PokemonType.FIRE) return 2.0;       // 水克火
            if (defender == PokemonType.WATER) return 0.5;      // 水抵抗水
            if (defender == PokemonType.GRASS) return 0.5;      // 水被草抵抗
            return 1.0;
        }

        // ===== 攻击方：草 =====
        if (attacker == PokemonType.GRASS) {
            if (defender == PokemonType.WATER) return 2.0;      // 草克水
            if (defender == PokemonType.FIRE) return 0.5;       // 草被火抵抗
            if (defender == PokemonType.GRASS) return 0.5;      // 草抵抗草
            if (defender == PokemonType.ELECTRIC) return 1.0;   // 草打电普通（非克制亦非抵抗）
            return 1.0;
        }

        // ===== 攻击方：电 =====
        if (attacker == PokemonType.ELECTRIC) {
            if (defender == PokemonType.WATER) return 2.0;      // 电克水
            if (defender == PokemonType.GRASS) return 0.5;      // 草抵抗电（电打草减半）
            if (defender == PokemonType.ELECTRIC) return 0.5;   // 电抵抗电
            return 1.0;
        }

        // ===== 攻击方：超能 =====
        // 超能对火/水/草/电均无克制或抵抗，全部1倍
        return 1.0;
    }

    public static String getDescription(double multiplier) {
        if (multiplier == 2.0) return "效果拔群！";
        if (multiplier == 0.5) return "效果不佳...";
        return "普通伤害。";
    }
}