import { GoogleLogin, type CredentialResponse } from "@react-oauth/google";
import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { toast } from "sonner";
import { useAuth } from "../hooks/useAuth";

import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useApiError } from "@/hooks/useApiError";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2Icon } from "lucide-react";
import { useForm } from "react-hook-form";
import * as z from "zod";

const formSchema = z.object({
    email: z.email("Enter a valid email address."),
    password: z.string().min(1, "Password is required."),
});

type FormData = z.infer<typeof formSchema>;

const LoginPage = () => {
    const { login, loginWithGoogle } = useAuth();
    const handleError = useApiError();
    const navigate = useNavigate();

    const [isLoading, setIsLoading] = useState(false);
    const [isGoogleLoading, setIsGoogleLoading] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<FormData>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            email: "",
            password: "",
        },
    });

    const handleLogin = async ({ email, password }: FormData) => {
        setIsLoading(true);
        try {
            await login(email, password);
            navigate("/events");
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
        } catch {
            toast.error("Google sign-in failed.");
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
                            Welcome back
                        </CardTitle>
                    </CardHeader>

                    <CardContent className="grid gap-5">
                        <form
                            onSubmit={handleSubmit(handleLogin)}
                            className="grid gap-4"
                        >
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
                                <div className="flex items-center justify-between gap-3">
                                    <Label htmlFor="password">Password</Label>
                                    <Link
                                        to="/forgot-password"
                                        className="text-xs font-medium text-muted-foreground underline-offset-4 hover:text-foreground hover:underline"
                                    >
                                        Forgot password?
                                    </Link>
                                </div>
                                <Input
                                    id="password"
                                    type="password"
                                    placeholder="••••••••"
                                    autoComplete="current-password"
                                    {...register("password")}
                                />
                                {errors.password && (
                                    <p className="text-sm text-destructive">
                                        {errors.password.message}
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
                                Sign in
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
                            Don't have an account?{" "}
                            <Link
                                to="/register"
                                className="font-medium text-foreground underline-offset-4 hover:text-foreground hover:underline"
                            >
                                Sign up
                            </Link>
                        </p>
                    </CardFooter>
                </Card>
            </div>
        </div>
    );
};

export default LoginPage;
