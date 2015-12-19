%% clear all
clc;
disp('Loading training data...');
% Load data
load('trainData.mat');
trX1 = X1;
trX2 = X2;
trX3 = X3;
trY = Y;

disp('Loading test data...');
load('testData.mat');
tstX1 = X1;
tstX2 = X2;
tstX3 = X3;
tstY = Y;

% Normalize the training data to [0,1]
disp('Normalize the training data to [0,1]...');
% calculate min values of every feature attribute
min1 = min(X1);
min2 = min(X2);
min3 = min(X3);
% calculate max values of every feature attribute
max1 = max(X1);
max2 = max(X2);
max3 = max(X3);
% convert the values to the range [0,1]
trX1 = bsxfun(@rdivide,bsxfun(@minus,trX1,min1),max1-min1);
trX2 = bsxfun(@rdivide,bsxfun(@minus,trX2,min2),max2-min2);
trX3 = bsxfun(@rdivide,bsxfun(@minus,trX3,min3),max3-min3);
% we are normalizing the test set using only the training set's min and max value 
tstX1 = bsxfun(@rdivide,bsxfun(@minus,tstX1,min1),max1-min1);
tstX2 = bsxfun(@rdivide,bsxfun(@minus,tstX2,min2),max2-min2);
tstX3 = bsxfun(@rdivide,bsxfun(@minus,tstX3,min3),max3-min3);

%% STEP 0: 
disp('Training models for individual features using linear SVM...');
disp('Training model for X1');
h1Step0 = svmtrain(trY, trX1, '-t 0 -c 10 -b 1 -q');
disp('Training model for X2');
h2Step0 = svmtrain(trY, trX2, '-t 0 -c 10 -b 1 -q');
disp('Training model for X3');
h3Step0 = svmtrain(trY, trX3, '-t 0 -c 10 -b 1 -q');
disp('Predicting the classification accuracy for test data for individual features with linear SVM models...');
disp('Predicting labels for h1');
[predY1Step0, accY1Step0, pY1Step0] = svmpredict(tstY, tstX1, h1Step0, '-b 1 -q');
fprintf('Prediction accuracy for linear SVM and feature 1 is: %f %%\n', accY1Step0(1));
disp('Predicting labels for h2');
[predY2Step0, accY2Step0, pY2Step0] = svmpredict(tstY, tstX2, h2Step0, '-b 1 -q');
fprintf('Prediction accuracy for linear SVM and feature 2 is: %f %%\n', accY2Step0(1));
disp('Predicting labels for h3');
[predY3Step0, accY3Step0, pY3Step0] = svmpredict(tstY, tstX3, h3Step0, '-b 1 -q');
fprintf('Prediction accuracy for linear SVM and feature 3 is: %f %%\n', accY3Step0(1));

%% STEP 1
pavgStep1 = zeros(size(pY1Step0));
for i = 1: size(pY1Step0,1)
    for j = 1:size(pY1Step0,2)
        pavgStep1(i,j) = (pY1Step0(i,j)+pY2Step0(i,j)+pY3Step0(i,j))/3;
    end
end

predYStep1 = zeros(size(pY1Step0,1),1);
for i = 1 : size(pavgStep1,1)
    maxVal = max(pavgStep1(i,:));
    [diff,index] = min(abs(pavgStep1(i,:)-maxVal));
    predYStep1(i,1) = index;
end

countStep1 = 0;
for i = 1 : size(predYStep1,1)
    if predYStep1(i) == tstY(i)
        countStep1 = countStep1 + 1;
    end
end

accYStep1 = (countStep1/size(predYStep1,1)) * 100;
fprintf('Prediction accuracy for linear SVM by taking mean of posterior probabilities of h1,h2,h3 is: %f %%\n', accYStep1);

%% STEP 2
trXStep2 = [trX1 trX2 trX3];
tstXStep2 = [tstX1 tstX2 tstX3];
disp('Training the linear cascaded model [X1 X2 X3]');
hStep2 = svmtrain(trY,trXStep2,'-t 0 -b 1 -c 10 -q');
disp('Predicting the classification accuracy of linear cascaded model');
[predYStep2,accYStep2,pYStep2] = svmpredict(tstY,tstXStep2,hStep2,'-b 1 -q');
fprintf('Prediction accuracy for linear cascaded model is: %f %%\n', accYStep2(1));


%% STEP 3
% Compute kernel matrices for training data
disp('Computing kernel matrix for training data of feature matrix 1...');
K1 = chi2Kernel(trX1,trX1);
disp('Computing kernel matrix for training data of feature matrix 2...');
K2 = chi2Kernel(trX2,trX2);
disp('Computing kernel matrix for training data of feature matrix 3...');
K3 = chi2Kernel(trX3,trX3);
% Compute kernel matrices for test data
disp('Computing kernel matrix for every pair of feature matrix 1''s test data with its train data...');
KK1 = chi2Kernel(tstX1,trX1);
disp('Computing kernel matrix for every pair of feature matrix 2''s test data with its train data...');
KK2 = chi2Kernel(tstX2,trX2);
disp('Computing kernel matrix for every pair of feature matrix 3''s test data with its train data...');
KK3 = chi2Kernel(tstX3,trX3);

% Prefixing serial number column in the kernel matrices
disp('Prefixing serial number column to the kernel matrices');
K1 = [(1:size(K1))' K1];
K2 = [(1:size(K2))' K2];
K3 = [(1:size(K3))' K3];
KK1 = [(1:size(KK1))' KK1];
KK2 = [(1:size(KK2))' KK2];
KK3 = [(1:size(KK3))' KK3];

% Building kernel models
disp('Training K1 kernel model');
modelK1 = svmtrain(trY,K1,'-t 4 -b 1 -c 10 -q');
disp('Training K2 kernel model');
modelK2 = svmtrain(trY,K2,'-t 4 -b 1 -c 10 -q');
disp('Training K3 kernel model');
modelK3 = svmtrain(trY,K3,'-t 4 -b 1 -c 10 -q');

% Predict classification results for the three models
disp('Predicting classification accuracy for the three models...')

[predK1Tr, accK1Tr, pK1Tr] = svmpredict(trY,K1,modelK1,'-q');
[predK1Tst, accK1Tst, pK1Tst] = svmpredict(tstY,KK1,modelK1,'-q');
disp('Classification accuracy for K1 model..');
disp(['On training data = ' num2str(accK1Tr(1)) ' %']);
disp(['On test data = ' num2str(accK1Tst(1)) ' %']);

[predK2Tr, accK2Tr, pK2Tr] = svmpredict(trY,K2,modelK2,'-q');
[predK2Tst, accK2Tst, pK2Tst] = svmpredict(tstY,KK2,modelK2,'-q');
disp('Classification accuracy for K2 model..');
disp(['On training data = ' num2str(accK2Tr(1)) ' %']);
disp(['On test data = ' num2str(accK2Tst(1)) ' %']);

[predK3Tr, accK3Tr, pK3Tr] = svmpredict(trY,K3,modelK3,'-q');
[predK3Tst, accK3Tst, pK3Tst] = svmpredict(tstY,KK3,modelK3,'-q');
disp('Classification accuracy for K3 model..');
disp(['On training data = ' num2str(accK3Tr(1)) ' %']);
disp(['On test data = ' num2str(accK3Tst(1)) ' %']);

% Fuse kernels
disp('Removing the first column containing the serial numbers from K1, K2, K3, KK1, KK2, KK3');
nK1 = K1(:,2:size(K1,2));
nK2 = K2(:,2:size(K2,2));
nK3 = K3(:,2:size(K3,2));
nKK1 = KK1(:,2:size(KK1,2));
nKK2 = KK2(:,2:size(KK2,2));
nKK3 = KK3(:,2:size(KK3,2));

disp('Fusing kernel matrices based on two types of models...');
Ka = (1/3).*(nK1 + nK2 + nK3);
Ktemp = nK1.*nK2.*nK3;
Kb = sign(Ktemp).*(abs(Ktemp).^(1/3));
KKa = (1/3).*(nKK1 + nKK2 + nKK3);
Ktemp = nKK1.*nKK2.*nKK3;
KKb = sign(Ktemp).*(abs(Ktemp).^(1/3));

% Prefixing serial number column in the kernel matrices
disp('Prefixing serial number column to the kernel matrices');
Ka = [(1:size(Ka))' Ka];
Kb = [(1:size(Kb))' Kb];
KKa = [(1:size(KKa))' KKa];
KKb = [(1:size(KKb))' KKb];

% Train model using kernel matrices
disp('Training model for Ka');
modelA = svmtrain(trY, Ka, '-t 4 -c 10 -b 1 -q');

disp('Training model for Kb');
modelB = svmtrain(trY, Kb, '-t 4 -c 10 -b 1 -q');


% Predict the classification accuracy for the fused model
[predATr, accATr, pATr] = svmpredict(trY, Ka, modelA);
[predATst, accATst, pATst] = svmpredict(tstY, KKa, modelA);
disp('Classification accuracy for model A..');
disp(['On training data = ' num2str(accATr(1)) ' %']);
disp(['On test data = ' num2str(accATst(1)) ' %']);

[predBTr, accBTr, pBTr] = svmpredict(trY, Kb, modelB);
[predBTst, accBTst, pBTst] = svmpredict(tstY, KKb, modelB);
disp('Classification accuracy for model B..');
disp(['On training data = ' num2str(accBTr(1)) ' %']);
disp(['On test data = ' num2str(accBTst(1)) ' %']);

disp('End of program...')