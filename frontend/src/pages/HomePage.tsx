import { Link } from "react-router";

import { buttonVariants } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { cn } from "@/lib/utils";

const HomePage = () => {
    return (
        <div className="min-h-dvh bg-gradient-to-b from-background to-muted/30">
            <div className="mx-auto flex min-h-dvh w-full max-w-5xl items-center justify-center px-4 py-12">
                <Card className="w-full max-w-lg bg-card/75 shadow-sm ring-1 ring-foreground/10 supports-backdrop-filter:backdrop-blur-sm">
                    <CardHeader className="text-center">
                        <div className="mx-auto inline-flex items-center gap-2 rounded-full border bg-muted/40 px-3 py-1 text-xs text-muted-foreground">
                            <span
                                aria-hidden="true"
                                className="size-1.5 rounded-full bg-primary"
                            />
                            Booking links, done right
                        </div>
                        <CardTitle className="mt-3 text-2xl font-semibold tracking-[-0.02em]">
                            Slotly
                        </CardTitle>
                        <CardDescription>
                            Schedule meetings, share booking links, and manage
                            availability.
                        </CardDescription>
                    </CardHeader>

                    <CardContent>
                        <div className="flex flex-col items-stretch justify-center gap-3 sm:flex-row sm:items-center">
                            <Link
                                to="/login"
                                className={cn(
                                    buttonVariants({ size: "lg" }),
                                    "w-full sm:w-auto",
                                )}
                            >
                                Sign in
                            </Link>
                            <Link
                                to="/register"
                                className={cn(
                                    buttonVariants({
                                        variant: "outline",
                                        size: "lg",
                                    }),
                                    "w-full sm:w-auto",
                                )}
                            >
                                Create account
                            </Link>
                        </div>
                    </CardContent>

                    <CardFooter className="justify-center">
                        <p className="text-xs text-muted-foreground">
                            Secure sign-in. Fast onboarding. Built for teams.
                        </p>
                    </CardFooter>
                </Card>
            </div>
        </div>
    );
};

export default HomePage;
