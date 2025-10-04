import { Loader2 } from "lucide-react";

export default function LoadingScreen() {
  return (
    <div className="min-h-screen bg-background flex items-center justify-center">
      <div className="text-center">
        <Loader2 className="h-12 w-12 animate-spin text-primary mx-auto mb-4" />
        <h2 className="text-xl font-semibold text-foreground mb-2">
          Loading Catalogizer
        </h2>
        <p className="text-muted-foreground">
          Initializing your media library...
        </p>
      </div>
    </div>
  );
}