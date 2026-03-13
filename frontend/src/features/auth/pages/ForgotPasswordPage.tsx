import axios from "axios";
import { useState } from "react";
import { Link } from "react-router";
import { toast } from "sonner";
import { ArrowLeft, Loader2Icon, Mail } from "lucide-react";
import { AuthApi } from "../api/AuthApi";

import FormField from "@/components/common/FormField";
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
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";

const formSchema = z.object({
    email: z.email("Enter a valid email address."),
});

type FormData = z.infer<typeof formSchema>;

const ForgotPasswordPage = () => {
    const [isLoading, setIsLoading] = useState(false);
    const [sent, setSent] = useState(false);

    const {
        register,
        handleSubmit,
        watch,
        formState: { errors },
    } = useForm<FormData>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            email: "",
        },
    });

    const getErrorMessage = (error: unknown) =>
        axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Something went wrong.")
            : "Something went wrong.";

    const onSubmit = async ({ email }: FormData) => {
        setIsLoading(true);
        try {
            await AuthApi.forgotPassword({ email });
            setSent(true);
        } catch (error) {
            toast.error(getErrorMessage(error));
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
                            Reset your password
                        </CardTitle>
                        <CardDescription>
                            {sent
                                ? "If the email exists, you'll receive a reset link shortly."
                                : "Enter your email and we'll send you a reset link."}
                        </CardDescription>
                    </CardHeader>

                    <CardContent>
                        {!sent ? (
                            <form
                                onSubmit={handleSubmit(onSubmit)}
                                className="grid gap-4"
                            >
                                <FormField id="email" label="Email" required>
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
                                </FormField>
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
                                    Send reset link
                                </Button>
                            </form>
                        ) : (
                            <div className="flex flex-col items-center gap-4 py-2">
                                <div className="flex size-12 items-center justify-center rounded-full bg-primary/10 text-primary">
                                    <Mail className="size-6" />
                                </div>
                                <div className="text-center">
                                    <div className="font-medium leading-snug">
                                        Check your inbox
                                    </div>
                                    <p className="mt-1 text-sm text-muted-foreground">
                                        We sent a reset link to{" "}
                                        <span className="font-medium text-foreground">
                                            {watch("email")}
                                        </span>
                                    </p>
                                </div>
                                <Button
                                    type="button"
                                    variant="ghost"
                                    size="sm"
                                    onClick={() => setSent(false)}
                                >
                                    Didn't receive it? Try again
                                </Button>
                            </div>
                        )}
                    </CardContent>

                    <CardFooter className="justify-center">
                        <Link
                            to="/login"
                            className="inline-flex items-center gap-2 text-sm font-medium text-muted-foreground underline-offset-4 hover:text-foreground hover:underline"
                        >
                            <ArrowLeft className="size-4" /> Back to sign in
                        </Link>
                    </CardFooter>
                </Card>
            </div>
        </div>
    );
};

export default ForgotPasswordPage;
