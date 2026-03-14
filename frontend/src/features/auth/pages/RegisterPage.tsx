import { GoogleLogin, type CredentialResponse } from "@react-oauth/google";
import { Eye, EyeOff, Loader2Icon } from "lucide-react";
import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { toast } from "sonner";
import { AuthApi } from "../api/AuthApi";
import { useAuth } from "../hooks/useAuth";

import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { useApiError } from "@/hooks/useApiError";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";

const formSchema = z.object({
    firstName: z
        .string()
        .min(1, "First name must not be empty.")
        .max(32, "First name must be at most 32 characters."),
    lastName: z
        .string()
        .min(1, "Last name must not be empty.")
        .max(32, "Last name must be at most 32 characters."),
    email: z.email("Enter a valid email address."),
    password: z
        .string()
        .min(8, "Password must be at least 8 characters.")
        .regex(
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$!%*?&])[A-Za-z\d@#$!%*?&]{8,}$/,
            "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.",
        ),
    timeZone: z.string().min(1),
});

const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
const timeZones = Intl.supportedValuesOf("timeZone");
type FormData = z.infer<typeof formSchema>;

const RegisterPage = () => {
    const { loginWithGoogle } = useAuth();
    const navigate = useNavigate();
    const handleError = useApiError();
    const [showPassword, setShowPassword] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [isGoogleLoading, setIsGoogleLoading] = useState(false);

    const {
        register,
        handleSubmit,
        setValue,
        watch,
        formState: { errors },
    } = useForm<FormData>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            firstName: "",
            lastName: "",
            email: "",
            password: "",
            timeZone: timeZone,
        },
    });

    const handleRegister = async (data: FormData) => {
        try {
            setIsLoading(true);
            await AuthApi.register(data);
            navigate("/verify-email", {
                state: { email: data.email, password: data.password },
            });
        } catch (error) {
            handleError(error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleGoogleSuccess = async ({ credential }: CredentialResponse) => {
        if (!credential) {
            toast.error("Google did not return an ID token.");
            return;
        }
        setIsGoogleLoading(true);
        try {
            await loginWithGoogle(credential);
            navigate("/events");
            toast.success("Successfully signed in.");
        } catch (error) {
            handleError(error);
        } finally {
            setIsGoogleLoading(false);
        }
    };

    return (
        <div className="min-h-dvh bg-linear-to-b from-background to-muted/30">
            <div className="mx-auto flex min-h-dvh w-full max-w-md flex-col justify-center px-4 py-12">
                <Card className="bg-card/75 shadow-sm ring-1 ring-foreground/10 supports-backdrop-filter:backdrop-blur-sm">
                    <CardHeader className="text-center">
                        <CardTitle className="mt-3 text-xl font-semibold tracking-[-0.02em]">
                            Create your account
                        </CardTitle>
                        <CardDescription>
                            Set up your profile and start sharing booking links.
                        </CardDescription>
                    </CardHeader>

                    <CardContent className="grid gap-5">
                        <form
                            onSubmit={handleSubmit(handleRegister)}
                            className="grid gap-4"
                        >
                            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                                <div className="grid gap-2">
                                    <Label htmlFor="firstName">
                                        First name
                                    </Label>
                                    <Input
                                        id="firstName"
                                        placeholder="John"
                                        autoComplete="given-name"
                                        {...register("firstName")}
                                    />
                                    {errors.firstName && (
                                        <p className="text-sm text-destructive">
                                            {errors.firstName.message}
                                        </p>
                                    )}
                                </div>

                                <div className="grid gap-2">
                                    <Label htmlFor="lastName">Last name</Label>
                                    <Input
                                        id="lastName"
                                        placeholder="Doe"
                                        autoComplete="family-name"
                                        {...register("lastName")}
                                    />
                                    {errors.lastName && (
                                        <p className="text-sm text-destructive">
                                            {errors.lastName.message}
                                        </p>
                                    )}
                                </div>
                            </div>

                            <div className="grid gap-2">
                                <Label htmlFor="email">Email</Label>
                                <Input
                                    id="email"
                                    type="email"
                                    placeholder="you@example.com"
                                    autoComplete="email"
                                    {...register("email")}
                                />
                                {errors.email && (
                                    <p className="text-sm text-destructive">
                                        {errors.email.message}
                                    </p>
                                )}
                            </div>

                            <div className="grid gap-2">
                                <Label htmlFor="password">Password</Label>
                                <div className="relative">
                                    <Input
                                        id="password"
                                        type={
                                            showPassword ? "text" : "password"
                                        }
                                        placeholder="••••••••"
                                        autoComplete="new-password"
                                        className="pr-10"
                                        {...register("password")}
                                    />
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="icon-sm"
                                        className="absolute right-1.5 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                        onClick={() =>
                                            setShowPassword((v) => !v)
                                        }
                                        aria-label={
                                            showPassword
                                                ? "Hide password"
                                                : "Show password"
                                        }
                                    >
                                        {showPassword ? (
                                            <EyeOff className="size-4" />
                                        ) : (
                                            <Eye className="size-4" />
                                        )}
                                    </Button>
                                </div>
                                {errors.password && (
                                    <p className="text-sm text-destructive">
                                        {errors.password.message}
                                    </p>
                                )}
                            </div>

                            <div className="grid gap-2">
                                <Label htmlFor="timeZone">Timezone</Label>
                                <Select
                                    value={watch("timeZone") || ""}
                                    onValueChange={(value) =>
                                        setValue("timeZone", value, {
                                            shouldValidate: true,
                                        })
                                    }
                                >
                                    <SelectTrigger
                                        id="timeZone"
                                        className="w-full"
                                    >
                                        <SelectValue placeholder="Select a timezone" />
                                    </SelectTrigger>
                                    <SelectContent align="start">
                                        <SelectGroup>
                                            {timeZones.map((tz) => (
                                                <SelectItem key={tz} value={tz}>
                                                    {tz}
                                                </SelectItem>
                                            ))}
                                        </SelectGroup>
                                    </SelectContent>
                                </Select>
                                {errors.timeZone && (
                                    <p className="text-sm text-destructive">
                                        {errors.timeZone.message}
                                    </p>
                                )}
                            </div>

                            <Button
                                type="submit"
                                className="w-full"
                                disabled={isLoading}
                            >
                                {isLoading && (
                                    <Loader2Icon
                                        aria-hidden="true"
                                        className="size-4 animate-spin"
                                    />
                                )}
                                Create account
                            </Button>
                        </form>

                        <div className="relative">
                            <div className="absolute inset-0 flex items-center">
                                <span className="w-full border-t" />
                            </div>
                            <div className="relative flex justify-center text-xs">
                                <span className="bg-card px-2 text-muted-foreground">
                                    or continue with
                                </span>
                            </div>
                        </div>

                        {isGoogleLoading ? (
                            <Button
                                variant="outline"
                                className="w-full"
                                disabled
                            >
                                <Loader2Icon className="size-4 animate-spin" />
                                Signing in with Google...
                            </Button>
                        ) : (
                            <div className="flex justify-center">
                                <GoogleLogin
                                    theme="filled_black"
                                    shape="rectangular"
                                    width="380"
                                    onSuccess={handleGoogleSuccess}
                                    onError={() =>
                                        toast.error("Google sign-in failed.")
                                    }
                                />
                            </div>
                        )}
                    </CardContent>

                    <CardFooter className="justify-center">
                        <p className="text-sm text-muted-foreground">
                            Already have an account?{" "}
                            <Link
                                to="/login"
                                className="font-medium text-foreground underline-offset-4 hover:text-foreground hover:underline"
                            >
                                Sign in
                            </Link>
                        </p>
                    </CardFooter>
                </Card>
            </div>
        </div>
    );
};

export default RegisterPage;
