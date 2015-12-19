% source - http://stackoverflow.com/questions/7177753/badresultwhenusingprecomputedchi2kernelwithlibsvmmatlab
function D = chi2Kernel(X,Y)
    % define a matrix of size X x Y
    D = zeros(size(X,1),size(Y,1));
    for i=1:size(Y,1)
        d = bsxfun(@minus, X, Y(i,:));
        s = bsxfun(@plus, X, Y(i,:));
        % calculate the chi square value for every pair
        D(:,i) = sum(d.^2 ./ (s/2+eps), 2);
    end
    D = 1 - D;
end