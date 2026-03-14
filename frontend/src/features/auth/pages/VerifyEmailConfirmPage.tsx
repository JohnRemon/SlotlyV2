import {
    Card,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { Loader2Icon, XIcon } from "lucide-react";
import { useEffect, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router";
import { AuthApi } from "../api/AuthApi";

type Status = "loading" | "error";

const VerifyEmailConfirmPage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const [status, setStatus] = useState<Status>(token ? "loading" : "error");
    const [message, setMessage] = useState(
        token ? "" : "No verification token found in the URL.",
    );
    const { setUser } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!token) {
            return;
        }

        AuthApi.verifyEmail(token)
            .then((response) => {
                setUser(response.data.data);
                navigate("/events", { replace: true });
            })
            .catch((error) => {
                setStatus("error");
                setMessage(
                    error?.response?.data?.message ??
                        "The link may be invalid or expired.",
                );
            });
    }, [navigate, setUser, token]);

    return (
        <div className="min-h-dvh bg-linear-to-b from-background to-muted/30">
            <div className="mx-auto flex min-h-dvh w-full max-w-md flex-col justify-center px-4 py-12">
                <Card className="bg-card/75 shadow-sm ring-1 ring-foreground/10 supports-backdrop-filter:backdrop-blur-sm">
                    <CardHeader className="text-center">
                        {status === "loading" && (
                            <>
                                <div className="mx-auto mb-3 flex size-12 items-center justify-center rounded-full bg-muted ring-1 ring-border">
                                    <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
                                </div>
                                <CardTitle className="text-xl font-semibold tracking-[-0.02em]">
                                    Verifying your email
                                </CardTitle>
                                <CardDescription>
                                    Please wait a moment...
                                </CardDescription>
                            </>
                        )}

                        {status === "error" && (
                            <>
                                <div className="mx-auto mb-3 flex size-12 items-center justify-center rounded-full bg-destructive/10 ring-1 ring-destructive/20">
                                    <XIcon className="size-5 text-destructive" />
                                </div>
                                <CardTitle className="text-xl font-semibold tracking-[-0.02em]">
                                    Verification failed
                                </CardTitle>
                                <CardDescription>
                                    {message}{" "}
                                    <Link
                                        to="/login"
                                        className="font-medium text-foreground underline-offset-4 hover:underline"
                                    >
                                        Back to sign in
                                    </Link>
                                </CardDescription>
                            </>
                        )}
                    </CardHeader>
                </Card>
            </div>
        </div>
    );
};

export default VerifyEmailConfirmPage;
