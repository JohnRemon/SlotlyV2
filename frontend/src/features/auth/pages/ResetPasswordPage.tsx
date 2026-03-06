import axios from "axios";
import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import toast from "react-hot-toast";
import { Eye, EyeOff } from "lucide-react";
import { resetPassword } from "../api/AuthApi";

const ResetPasswordPage = () => {
    const { token } = useParams<{ token: string }>();
    const navigate = useNavigate();

    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [showNew, setShowNew] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const passwordsMatch =
        confirmPassword.length > 0 && password === confirmPassword;
    const passwordsMismatch =
        confirmPassword.length > 0 && password !== confirmPassword;

    const getErrorMessage = (error: unknown) =>
        axios.isAxiosError(error)
            ? (error.response?.data?.message ?? "Something went wrong.")
            : "Something went wrong.";

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            toast.error("Passwords do not match.");
            return;
        }

        setIsLoading(true);
        try {
            await resetPassword(token!, password, confirmPassword);
            toast.success("Password changed successfully.");
            navigate("/login");
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
                    <p className="text-sm text-base-content/60">
                        Enter your new password.
                    </p>

                    <form
                        onSubmit={handleSubmit}
                        className="flex flex-col gap-4"
                    >
                        {/* New Password */}
                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-medium text-base-content">
                                New password
                            </label>
                            <div className="relative">
                                <input
                                    type={showNew ? "text" : "password"}
                                    className="input input-bordered w-full pr-10"
                                    placeholder="••••••••"
                                    value={password}
                                    onChange={(e) =>
                                        setPassword(e.target.value)
                                    }
                                    required
                                />
                                <button
                                    type="button"
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-base-content/40 hover:text-base-content transition-colors"
                                    onClick={() => setShowNew((v) => !v)}
                                >
                                    {showNew ? (
                                        <EyeOff className="w-4 h-4" />
                                    ) : (
                                        <Eye className="w-4 h-4" />
                                    )}
                                </button>
                            </div>
                        </div>

                        {/* Confirm Password */}
                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-medium text-base-content">
                                Confirm password
                            </label>
                            <div className="relative">
                                <input
                                    type={showConfirm ? "text" : "password"}
                                    className={`input input-bordered w-full pr-10 ${
                                        passwordsMismatch
                                            ? "input-error"
                                            : passwordsMatch
                                              ? "input-success"
                                              : ""
                                    }`}
                                    placeholder="••••••••"
                                    value={confirmPassword}
                                    onChange={(e) =>
                                        setConfirmPassword(e.target.value)
                                    }
                                    required
                                />
                                <button
                                    type="button"
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-base-content/40 hover:text-base-content transition-colors"
                                    onClick={() => setShowConfirm((v) => !v)}
                                >
                                    {showConfirm ? (
                                        <EyeOff className="w-4 h-4" />
                                    ) : (
                                        <Eye className="w-4 h-4" />
                                    )}
                                </button>
                            </div>

                            {/* Match indicator */}
                            {passwordsMatch && (
                                <p className="text-xs text-success">
                                    Passwords match
                                </p>
                            )}
                            {passwordsMismatch && (
                                <p className="text-xs text-error">
                                    Passwords do not match
                                </p>
                            )}
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary w-full mt-1"
                            disabled={isLoading}
                        >
                            {isLoading ? (
                                <span className="loading loading-spinner loading-sm" />
                            ) : (
                                "Reset password"
                            )}
                        </button>
                    </form>
                </div>

                <Link
                    to="/login"
                    className="flex items-center justify-center gap-1.5 text-sm text-base-content/50 hover:text-base-content transition-colors"
                >
                    Back to sign in
                </Link>
            </div>
        </div>
    );
};

export default ResetPasswordPage;
