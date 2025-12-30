package net.azisaba.afnw.afnwcore2.util;

import net.azisaba.afnw.afnwcore2.AfnwCore2;
import org.bukkit.BanEntry;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.expression.CompileData;
import xyz.acrylicstyle.util.expression.ExpressionParser;
import xyz.acrylicstyle.util.expression.RuntimeData;
import xyz.acrylicstyle.util.expression.instruction.DummyInstTypeInfo;
import xyz.acrylicstyle.util.expression.instruction.Instruction;
import xyz.acrylicstyle.util.expression.instruction.InstructionSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class Expr {
    public static final Expr INSTANCE = new Expr();
    private static final Set<Class<?>> DISALLOWED_RETURN_TYPES = new HashSet<>(Arrays.asList(
            void.class, ConsoleCommandSender.class, Class.class, Field.class, Method.class, Constructor.class,
            BanEntry.class
    ));
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("\\$\\{(.+)}");

    @Nullable
    public static String evalAndReplace(Player player, String line, String description) {
        AtomicBoolean modified = new AtomicBoolean(false);
        Matcher matcher = SCRIPT_PATTERN.matcher(line);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            modified.set(true);
            try {
                matcher.appendReplacement(sb, String.valueOf(Expr.eval(player, matcher.group(1))));
            } catch (Exception e) {
                matcher.appendReplacement(sb, "<ERROR>");
                AfnwCore2.getPluginLogger().error("Error evaluating script (description: {})\nLine: {}", description, line, e);
            }
        }
        matcher.appendTail(sb);
        return modified.get() ? sb.toString() : null;
    }

    @NotNull
    public static Stream<String> getSuggestions(Object sender, String src) {
        try {
            CompileData compileData =
                    CompileData.builder()
                            .allowPrivate(true)
                            .addVariable("player", sender.getClass())
                            .addVariable("expr", Expr.class)
                            .build();
            return getSuggestionsPartial(Stream.of("player", "expr"), compileData, src);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull Stream<String> getSuggestionsFull(@NotNull Stream<String> variables, @NotNull CompileData compileData, @NotNull String input) throws Exception {
        if (!input.contains(".")) {
            return variables;
        }
        InstructionSet instructionSet = ExpressionParser.compile(input.substring(0, input.lastIndexOf('.')), compileData);
        Instruction instruction = instructionSet.lastOrNull();
        if (instruction instanceof DummyInstTypeInfo typeInfo) {
            return getTokens(typeInfo.getClazz()).map(s -> input.substring(0, input.lastIndexOf('.') + 1) + s);
        }
        return Stream.empty();
    }

    public static @NotNull Stream<String> getSuggestionsPartial(@NotNull Stream<String> variables, @NotNull CompileData compileData, @NotNull String input) throws Exception {
        if (!input.contains(".")) {
            return variables.filter(s -> s.startsWith(input));
        }
        String token = input.substring(input.lastIndexOf('.') + 1);
        String[] args = input.split(" ");
        String last = args[args.length - 1];
        InstructionSet instructionSet = ExpressionParser.compile(input.substring(0, input.lastIndexOf('.')), compileData);
        Instruction instruction = instructionSet.lastOrNull();
        if (instruction instanceof DummyInstTypeInfo typeInfo) {
            return getTokens(typeInfo.getClazz())
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(token.toLowerCase(Locale.ROOT)))
                    .map(s -> last.substring(0, last.lastIndexOf('.') + 1) + s);
        }
        return Stream.empty();
    }

    private static Stream<String> getTokens(@NotNull Class<?> type) {
        List<String> tokens = new ArrayList<>();
        for (Class<?> clazz : getSupers(type)) {
            for (Field field : clazz.getDeclaredFields()) {
                tokens.add(field.getName());
            }
            for (Method method : clazz.getDeclaredMethods()) {
                if (DISALLOWED_RETURN_TYPES.contains(method.getReturnType())) continue;
                if (method.getName().length() >= 4
                        && method.getName().startsWith("get")
                        && Character.isUpperCase(method.getName().charAt(3))
                        && method.getParameterCount() == 0) {
                    tokens.add(method.getName().substring(3, 4).toLowerCase(Locale.ROOT) + method.getName().substring(4));
                }
                if (method.getParameterCount() == 0) {
                    tokens.add(method.getName() + "()");
                    tokens.add(method.getName());
                } else {
                    tokens.add(method.getName() + "(");
                }
            }
        }
        tokens.add("?as(");
        return tokens.stream().distinct();
    }

    private static @NotNull Set<Class<?>> getSupers(@NotNull Class<?> type) {
        Set<Class<?>> list = new LinkedHashSet<>();
        list.add(type);
        if (type.getSuperclass() != null) {
            list.add(type.getSuperclass());
            list.addAll(getSupers(type.getSuperclass()));
        }
        list.addAll(Arrays.asList(type.getInterfaces()));
        for (Class<?> anInterface : type.getInterfaces()) {
            list.addAll(getSupers(anInterface));
        }
        return list;
    }

    public static @Nullable Object eval(@NotNull Player player, @NotNull String src) {
        try {
            CompileData compileData =
                    CompileData.builder()
                            .allowPrivate(true)
                            .addVariable("player", player.getClass())
                            .addVariable("expr", Expr.class)
                            .build();
            InstructionSet instructionSet = ExpressionParser.compile(src, compileData);
            instructionSet.forEach(instruction -> {
                if (instruction instanceof DummyInstTypeInfo) {
                    Class<?> clazz = ((DummyInstTypeInfo) instruction).getClazz();
                    if (DISALLOWED_RETURN_TYPES.contains(clazz)) {
                        throw new RuntimeException("Disallowed return type: " + clazz);
                    }
                }
            });
            return instructionSet.execute(
                    RuntimeData.builder()
                            .allowPrivate(true)
                            .addVariable("player", player)
                            .addVariable("expr", INSTANCE)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long round(double d) {
        return Math.round(d);
    }

    public double round1(double d) {
        return round(d, 1);
    }

    public double round2(double d) {
        return round(d, 2);
    }

    public double round3(double d) {
        return round(d, 3);
    }

    public double round4(double d) {
        return round(d, 4);
    }

    public double round5(double d) {
        return round(d, 5);
    }

    public double round(double d, int points) {
        return Math.round(d * Math.pow(10, points)) / Math.pow(10, points);
    }

    public double randomDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public double secureRandomDouble(double min, double max) {
        return SECURE_RANDOM.nextDouble() * (max - min) + min;
    }

    public double randomDouble(int min, int max) {
        return Math.random() * (max - min) + min;
    }

    public double secureRandomDouble(int min, int max) {
        return SECURE_RANDOM.nextDouble() * (max - min) + min;
    }

    public long randomLong(long min, long max) {
        return Math.round(randomDouble(min, max));
    }

    public long secureRandomLong(long min, long max) {
        return Math.round(secureRandomDouble(min, max));
    }

    public int randomInt(int min, int max) {
        return Math.toIntExact(Math.round(randomDouble(min, max)));
    }

    public int secureRandomInt(int min, int max) {
        return Math.toIntExact(Math.round(secureRandomDouble(min, max)));
    }

    @NotNull
    @Contract(pure = true)
    public static String longToHex(long l) {
        return Long.toString(l, 16);
    }
}
