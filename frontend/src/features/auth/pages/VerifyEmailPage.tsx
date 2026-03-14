import {
    Card,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { useApiError } from "@/hooks/useApiError";
import { Loader2Icon, MailIcon } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { AuthApi } from "../api/AuthApi";
import { useLocation } from "react-router";

const VerifyEmailPage = () => {
    const handleError = useApiError();
    const [isResending, setIsResending] = useState(false);

    const { state } = useLocation();

    const handleResend = async () => {
        if (!state?.email) {
            toast.error("Email not found. Please register again.");
            return;
        }
        setIsResending(true);
        try {
            await AuthApi.resendVerificationEmail(state?.email);
            toast.success("Verification email resent.");
        } catch (error) {
            handleError(error);
        } finally {
            setIsResending(false);
        }
    };

    return (
        <div className="min-h-dvh bg-linear-to-b from-background to-muted/30">
            <div className="mx-auto flex min-h-dvh w-full max-w-md flex-col justify-center px-4 py-12">
                <Card className="bg-card/75 shadow-sm ring-1 ring-foreground/10 supports-backdrop-filter:backdrop-blur-sm">
                    <CardHeader className="text-center">
                        <div className="mx-auto mb-3 flex size-12 items-center justify-center rounded-full bg-muted ring-1 ring-border">
                            <MailIcon className="size-5 text-muted-foreground" />
                        </div>
                        <CardTitle className="text-xl font-semibold tracking-[-0.02em]">
                            Check your email
                        </CardTitle>
                        <CardDescription>
                            We sent a verification link to your email address.
                            Click the link to verify your account and get
                            started.
                        </CardDescription>
                    </CardHeader>

                    <CardFooter className="justify-center">
                        <p className="text-sm text-muted-foreground">
                            Didn't receive it?{" "}
                            <button
                                type="button"
                                disabled={isResending}
                                className="font-medium text-foreground underline-offset-4 hover:underline disabled:pointer-events-none disabled:opacity-50"
                                onClick={handleResend}
                            >
                                {isResending ? (
                                    <span className="inline-flex items-center gap-1">
                                        <Loader2Icon className="size-3 animate-spin" />
                                        Resending...
                                    </span>
                                ) : (
                                    "Resend email"
                                )}
                            </button>
                        </p>
                    </CardFooter>
                </Card>
            </div>
        </div>
    );
};

export default VerifyEmailPage;
