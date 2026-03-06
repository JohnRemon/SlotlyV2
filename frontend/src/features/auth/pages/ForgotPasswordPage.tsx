import axios from "axios";
import { useState } from "react";
import { Link } from "react-router";
import toast from "react-hot-toast";
import { ArrowLeft, Mail } from "lucide-react";
import { forgotPassword } from "../api/AuthApi";

const ForgotPasswordPage = () => {
    const [email, setEmail] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [sent, setSent] = useState(false);

    const getErrorMessage = (error: unknown) =>
        axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Something went wrong.")
            : "Something went wrong.";

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await forgotPassword(email);
            setSent(true);
        } catch (error) {
            toast.error(getErrorMessage(error));
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-base-200 flex items-center justify-center px-4">
            <div className="w-full max-w-sm flex flex-col gap-6">
                <div className="bg-base-100 rounded-2xl shadow-sm border border-base-300 p-8 flex flex-col gap-5">
                    {!sent ? (
                        <>
                            <p className="text-sm text-base-content/60">
                                Enter your email and we'll send you a link to
                                reset your password.
                            </p>
                            <form
                                onSubmit={handleSubmit}
                                className="flex flex-col gap-4"
                            >
                                <div className="flex flex-col gap-1.5">
                                    <label className="text-sm font-medium text-base-content">
                                        Email
                                    </label>
                                    <input
                                        type="email"
                                        className="input input-bordered w-full"
                                        placeholder="you@example.com"
                                        value={email}
                                        onChange={(e) =>
                                            setEmail(e.target.value)
                                        }
                                        required
                                    />
                                </div>
                                <button
                                    type="submit"
                                    className="btn btn-primary w-full"
                                    disabled={isLoading}
                                >
                                    {isLoading ? (
                                        <span className="loading loading-spinner loading-sm" />
                                    ) : (
                                        "Send reset link"
                                    )}
                                </button>
                            </form>
                        </>
                    ) : (
                        // Success State
                        <div className="flex flex-col items-center gap-4 py-2">
                            <div className="w-12 h-12 rounded-full bg-primary/10 text-primary flex items-center justify-center">
                                <Mail className="w-6 h-6" />
                            </div>
                            <div className="text-center">
                                <p className="font-semibold text-base-content">
                                    Check your inbox
                                </p>
                                <p className="text-sm text-base-content/50 mt-1">
                                    We sent a reset link to{" "}
                                    <span className="font-medium text-base-content">
                                        {email}
                                    </span>
                                </p>
                            </div>
                            <button
                                className="btn btn-ghost btn-sm"
                                onClick={() => setSent(false)}
                            >
                                Didn't receive it? Try again
                            </button>
                        </div>
                    )}
                </div>

                <Link
                    to="/login"
                    className="flex items-center justify-center gap-1.5 text-sm text-base-content/50 hover:text-base-content transition-colors"
                >
                    <ArrowLeft className="w-4 h-4" /> Back to sign in
                </Link>
            </div>
        </div>
    );
};

export default ForgotPasswordPage;
