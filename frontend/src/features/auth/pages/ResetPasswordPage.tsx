import { Eye, EyeOff, Loader2Icon } from "lucide-react";
import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { toast } from "sonner";
import { AuthApi } from "../api/AuthApi";

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
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";

const formSchema = z
    .object({
        password: z
            .string()
            .min(8, "Password must be at least 8 characters.")
            .regex(
                /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
                "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.",
            ),
        confirmPassword: z
            .string()
            .min(1, "Please confirm your password."),
    })
    .refine((data) => data.password === data.confirmPassword, {
        path: ["confirmPassword"],
        message: "Passwords do not match.",
    });

type FormData = z.infer<typeof formSchema>;

const ResetPasswordPage = () => {
    const { token } = useParams<{ token: string }>();
    const navigate = useNavigate();

    const [showNew, setShowNew] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const {
        register,
        handleSubmit,
        watch,
        formState: { errors },
    } = useForm<FormData>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            password: "",
            confirmPassword: "",
        },
    });

    const password = watch("password") || "";
    const confirmPassword = watch("confirmPassword") || "";

    const passwordsMatch =
        confirmPassword.length > 0 && password === confirmPassword;
    const passwordsMismatch =
        confirmPassword.length > 0 && password !== confirmPassword;

    const onSubmit = async ({ password, confirmPassword }: FormData) => {
        setIsLoading(true);
        try {
            await AuthApi.resetPassword(
                {
                    password,
                    confirmPassword,
                },
                token!,
            );
            toast.success("Password changed successfully.");
            navigate("/login");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-dvh bg-gradient-to-b from-background to-muted/30">
            <div className="mx-auto flex min-h-dvh w-full max-w-md flex-col justify-center px-4 py-12">
                <Card className="bg-card/75 shadow-sm ring-1 ring-foreground/10 supports-backdrop-filter:backdrop-blur-sm">
                    <CardHeader className="text-center">
                        <div className="mx-auto inline-flex items-center gap-2 rounded-full border bg-muted/40 px-3 py-1 text-xs text-muted-foreground">
                            <span
                                aria-hidden="true"
                                className="size-1.5 rounded-full bg-primary"
                            />
                            Slotly
                        </div>
                        <CardTitle className="mt-3 text-xl font-semibold tracking-[-0.02em]">
                            Choose a new password
                        </CardTitle>
                        <CardDescription>
                            Enter and confirm your new password to finish the
                            reset.
                        </CardDescription>
                    </CardHeader>

                    <CardContent>
                        <form
                            onSubmit={handleSubmit(onSubmit)}
                            className="grid gap-4"
                        >
                            <div className="grid gap-2">
                                <Label htmlFor="newPassword">
                                    New password
                                </Label>
                                <div className="relative">
                                    <Input
                                        id="newPassword"
                                        type={showNew ? "text" : "password"}
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
                                        onClick={() => setShowNew((v) => !v)}
                                        aria-label={
                                            showNew
                                                ? "Hide password"
                                                : "Show password"
                                        }
                                    >
                                        {showNew ? (
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
                                <Label htmlFor="confirmPassword">
                                    Confirm password
                                </Label>
                                <div className="relative">
                                    <Input
                                        id="confirmPassword"
                                        type={
                                            showConfirm ? "text" : "password"
                                        }
                                        placeholder="••••••••"
                                        autoComplete="new-password"
                                        {...register("confirmPassword")}
                                        aria-invalid={
                                            !!errors.confirmPassword || undefined
                                        }
                                        className={
                                            errors.confirmPassword
                                                ? "pr-10 border-destructive focus-visible:border-destructive focus-visible:ring-destructive/20"
                                                : "pr-10"
                                        }
                                    />
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="icon-sm"
                                        className="absolute right-1.5 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                        onClick={() =>
                                            setShowConfirm((v) => !v)
                                        }
                                        aria-label={
                                            showConfirm
                                                ? "Hide password"
                                                : "Show password"
                                        }
                                    >
                                        {showConfirm ? (
                                            <EyeOff className="size-4" />
                                        ) : (
                                            <Eye className="size-4" />
                                        )}
                                    </Button>
                                </div>

                                {passwordsMatch && (
                                    <p className="text-xs text-primary">
                                        Passwords match
                                    </p>
                                )}
                                {passwordsMismatch && !errors.confirmPassword && (
                                    <p className="text-xs text-destructive">
                                        Passwords do not match
                                    </p>
                                )}
                                {errors.confirmPassword && (
                                    <p className="text-sm text-destructive">
                                        {errors.confirmPassword.message}
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
                                Reset password
                            </Button>
                        </form>
                    </CardContent>

                    <CardFooter className="justify-center">
                        <Link
                            to="/login"
                            className="text-sm font-medium text-muted-foreground underline-offset-4 hover:text-foreground hover:underline"
                        >
                            Back to sign in
                        </Link>
                    </CardFooter>
                </Card>
            </div>
        </div>
    );
};

export default ResetPasswordPage;
